package jp.i432kg.footprint.presentation.api;

import jakarta.validation.Valid;
import jp.i432kg.footprint.application.query.PostQueryService;
import jp.i432kg.footprint.application.query.ReplyQueryService;
import jp.i432kg.footprint.application.query.model.PostSummary;
import jp.i432kg.footprint.application.query.model.ReplySummary;
import jp.i432kg.footprint.application.service.PostApplicationService;
import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.value.Comment;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.SearchKeyword;
import jp.i432kg.footprint.infrastructure.datasource.impl.UserDetailsImpl;
import jp.i432kg.footprint.presentation.api.dto.PostRequest;
import jp.i432kg.footprint.presentation.api.response.PostItemResponse;
import jp.i432kg.footprint.presentation.api.response.ReplyItemResponse;
import jp.i432kg.footprint.presentation.api.response.mapper.PostResponseMapper;
import jp.i432kg.footprint.presentation.api.response.mapper.ReplyResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 投稿に関する操作を提供する API コントローラー
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostRestController {

    private final PostApplicationService postApplicationService;

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
            @RequestParam(required = false) final PostId lastId,
            @RequestParam(defaultValue = "10") final int size) {

        // 投稿一覧を取得する
        List<PostSummary> postSummaries = postQueryService.listRecentPosts(lastId, size);

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
            @RequestParam final SearchKeyword keyword,
            @RequestParam(required = false) final PostId lastId,
            @RequestParam(defaultValue = "10") int size) {

        // 検索結果を取得する
        List<PostSummary> postSummaries = postQueryService.searchPosts(keyword, lastId, size);

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
    public ResponseEntity<PostItemResponse> getPost(@PathVariable final PostId postId) {

        // 投稿詳細を取得する
        PostSummary postSummary = postQueryService.getPost(postId);

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
    public ResponseEntity<List<ReplyItemResponse>> getReplies(@PathVariable final PostId postId) {

        // 返信一覧を取得する
        final List<ReplySummary> replySummaries = replyQueryService.listTopLevelReplies(postId);

        // レスポンス形式に変換する
        final List<ReplyItemResponse> responses = replyResponseMapper.fromList(replySummaries);

        return ResponseEntity.ok(responses);
    }

    // 投稿を作成
    @PostMapping
    public ResponseEntity<Void> create(
            @Valid final PostRequest request,
            @AuthenticationPrincipal final UserDetailsImpl userDetails
    ) {

        final Post.NewPost newPost = Post.newPost()
                .userId(userDetails.getUserId())
                .comment(new Comment(request.getComment()))
                .build();

        postApplicationService.createPost(newPost, request.getImageFile());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
