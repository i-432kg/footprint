package jp.i432kg.footprint.presentation.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jp.i432kg.footprint.application.command.PostCommandService;
import jp.i432kg.footprint.application.command.model.CreatePostCommand;
import jp.i432kg.footprint.application.query.PostQueryService;
import jp.i432kg.footprint.application.query.ReplyQueryService;
import jp.i432kg.footprint.application.query.model.PostSummary;
import jp.i432kg.footprint.application.query.model.ReplySummary;
import jp.i432kg.footprint.domain.value.*;
import jp.i432kg.footprint.infrastructure.security.UserDetailsImpl;
import jp.i432kg.footprint.presentation.api.request.PostRequest;
import jp.i432kg.footprint.presentation.api.response.PostItemResponse;
import jp.i432kg.footprint.presentation.api.response.ReplyItemResponse;
import jp.i432kg.footprint.presentation.api.response.mapper.PostResponseMapper;
import jp.i432kg.footprint.presentation.api.response.mapper.ReplyResponseMapper;
import jp.i432kg.footprint.presentation.validation.PresentationValidationPatterns;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 投稿に関する操作を提供する API コントローラー
 */
@RestController
@RequestMapping("/api/posts")
@Validated
@RequiredArgsConstructor
public class PostRestController {

    private final PostCommandService postCommandService;

    private final PostQueryService postQueryService;

    private final ReplyQueryService replyQueryService;

    private final PostResponseMapper postResponseMapper;

    private final ReplyResponseMapper replyResponseMapper;

    /**
     * 最新の投稿一覧を取得します。
     *
     * @param lastId 最後に取得した投稿の識別子（スクロール読み込み用、任意）
     * @param size   取得する投稿件数（デフォルト 10件）
     * @return 投稿アイテムのリスト
     */
    @GetMapping
    public ResponseEntity<List<PostItemResponse>> getRecentPosts(
            @RequestParam(required = false) @Pattern(regexp = PresentationValidationPatterns.ULID) final String lastId,
            @RequestParam(defaultValue = "10") @Min(1) @Max(20) final int size) {

        // 投稿一覧を取得する
        final List<PostSummary> postSummaries = postQueryService.listRecentPosts(toPostId(lastId), size);

        // レスポンス形式に変換する
        List<PostItemResponse> responses = postResponseMapper.fromList(postSummaries);

        return ResponseEntity.ok(responses);
    }

    /**
     * キーワードに基づいて投稿を検索します。
     *
     * @param keyword 検索キーワード
     * @param lastId  最後に取得した投稿の識別子（スクロール読み込み用、任意）
     * @param size    取得する投稿件数（デフォルト 10件）
     * @return 検索結果の投稿アイテムリスト
     */
    @GetMapping("/search")
    public ResponseEntity<List<PostItemResponse>> search(
            @RequestParam @NotBlank @Size(max = 100)
            @Pattern(regexp = PresentationValidationPatterns.NO_CONTROL_CHARS) final String keyword,
            @RequestParam(required = false) @Pattern(regexp = PresentationValidationPatterns.ULID) final String lastId,
            @RequestParam(defaultValue = "10") @Min(1) @Max(20) int size) {

        // 検索結果を取得する
        final List<PostSummary> postSummaries = postQueryService.searchPosts(
                SearchKeyword.of(keyword),
                toPostId(lastId),
                size
        );

        // レスポンス形式に変換する
        List<PostItemResponse> responses = postResponseMapper.fromList(postSummaries);

        return ResponseEntity.ok(responses);
    }

    /**
     * 指定された範囲内の投稿を検索します。
     *
     * @param minLat 最小緯度
     * @param maxLat 最大緯度
     * @param minLng 最小経度
     * @param maxLng 最大経度
     * @return 検索結果の投稿アイテムリスト
     */
    @GetMapping("/search/map")
    public ResponseEntity<List<PostItemResponse>> searchMap(
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") final BigDecimal minLat,
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") final BigDecimal maxLat,
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") final BigDecimal minLng,
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") final BigDecimal maxLng) {

        // 検索結果を取得する
        final List<PostSummary> postSummaries = postQueryService.searchPostsByBBox(
                Latitude.of(minLat),
                Latitude.of(maxLat),
                Longitude.of(minLng),
                Longitude.of(maxLng)
        );

        // レスポンス形式に変換する
        List<PostItemResponse> responses = postResponseMapper.fromList(postSummaries);

        return ResponseEntity.ok(responses);
    }

    /**
     * 指定された投稿IDの詳細情報を取得します。
     *
     * @param postId 投稿の識別子
     * @return 投稿詳細のレスポンス
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostItemResponse> getPost(
            @PathVariable @Pattern(regexp = PresentationValidationPatterns.ULID) final String postId
    ) {

        // 投稿詳細を取得する
        final PostSummary postSummary = postQueryService.getPost(PostId.of(postId));

        // レスポンス形式に変換する
        PostItemResponse response = postResponseMapper.from(postSummary);

        return ResponseEntity.ok(response);
    }

    /**
     * 指定された投稿IDに紐づくトップレベルの返信一覧を取得します。
     *
     * @param postId 投稿の識別子
     * @return 返信アイテムのリスト
     */
    @GetMapping("/{postId}/replies")
    public ResponseEntity<List<ReplyItemResponse>> getReplies(
            @PathVariable @Pattern(regexp = PresentationValidationPatterns.ULID) final String postId
    ) {

        // 返信一覧を取得する
        final List<ReplySummary> replySummaries = replyQueryService.listTopLevelReplies(PostId.of(postId));

        // レスポンス形式に変換する
        final List<ReplyItemResponse> responses = replyResponseMapper.fromList(replySummaries);

        return ResponseEntity.ok(responses);
    }

    /**
     * 新しい投稿を作成します。
     *
     * @param request     投稿作成リクエスト
     * @param userDetails 認証済みユーザーの詳細情報
     * @return 投稿作成結果
     * @throws IOException 画像処理時に発生した例外
     */
    @PostMapping
    public ResponseEntity<Void> create(
            @Valid final PostRequest request,
            @AuthenticationPrincipal final UserDetailsImpl userDetails
    ) throws IOException {

        // リクエスト情報をコマンド形式に変換する
        final CreatePostCommand command = CreatePostCommand.of(
                userDetails.getUserId(),
                PostComment.of(Objects.requireNonNullElse(request.getComment(), "")),
                request.getImageFile().getInputStream(),
                FileName.of(request.getImageFile().getOriginalFilename())
        );

        // 投稿情報を保存する
        postCommandService.createPost(command);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private PostId toPostId(final String rawPostId) {
        return rawPostId == null ? null : PostId.of(rawPostId);
    }
}
