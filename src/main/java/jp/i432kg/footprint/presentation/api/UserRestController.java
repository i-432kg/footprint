package jp.i432kg.footprint.presentation.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jp.i432kg.footprint.application.command.service.UserCommandService;
import jp.i432kg.footprint.application.command.model.CreateUserCommand;
import jp.i432kg.footprint.application.query.service.PostQueryService;
import jp.i432kg.footprint.application.query.service.ReplyQueryService;
import jp.i432kg.footprint.application.query.service.UserQueryService;
import jp.i432kg.footprint.application.query.model.PostSummary;
import jp.i432kg.footprint.application.query.model.ReplySummary;
import jp.i432kg.footprint.application.query.model.UserProfileSummary;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.domain.value.UserName;
import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.domain.value.RawPassword;
import jp.i432kg.footprint.domain.value.BirthDate;
import jp.i432kg.footprint.infrastructure.security.UserDetailsImpl;
import jp.i432kg.footprint.presentation.api.request.SignUpRequest;
import jp.i432kg.footprint.presentation.api.response.PostItemResponse;
import jp.i432kg.footprint.presentation.api.response.ReplyItemResponse;
import jp.i432kg.footprint.presentation.api.response.UserProfileItemResponse;
import jp.i432kg.footprint.presentation.api.response.mapper.PostResponseMapper;
import jp.i432kg.footprint.presentation.api.response.mapper.ReplyResponseMapper;
import jp.i432kg.footprint.presentation.api.response.mapper.UserProfileResponseMapper;
import jp.i432kg.footprint.presentation.validation.PresentationValidationPatterns;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/**
 * ユーザーに関する情報を管理する API コントローラー
 */
@RestController
@RequestMapping("/api/users")
@Validated
@RequiredArgsConstructor
public class UserRestController {

    private final PostQueryService postQueryService;

    private final ReplyQueryService replyQueryService;

    private final UserQueryService userQueryService;

    private final UserCommandService userCommandService;

    private final Clock clock;

    private final PostResponseMapper postResponseMapper;

    private final ReplyResponseMapper replyResponseMapper;

    private final UserProfileResponseMapper userProfileResponseMapper;

    /**
     * 現在ログインしているユーザーのプロフィール情報を取得します。
     *
     * @param userDetails 認証済みユーザーの詳細情報
     * @return ユーザープロフィールのレスポンス
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileItemResponse> getCurrentUser(
            @AuthenticationPrincipal final UserDetailsImpl userDetails) {

        // ユーザーのプロフィール情報を取得する
        final UserProfileSummary userProfile = userQueryService.getUserProfile(userDetails.getUserId());

        // レスポンス形式に変換する
        final UserProfileItemResponse response = userProfileResponseMapper.from(userProfile);

        return ResponseEntity.ok(response);
    }

    /**
     * 自分の投稿一覧を取得します。
     *
     * @param lastId      最後の投稿 ID
     * @param size        取得する投稿数
     * @param userDetails 認証済みユーザーの詳細情報
     * @return 自分の投稿一覧のレスポンス
     */
    @GetMapping("/me/posts")
    public ResponseEntity<List<PostItemResponse>> getMyPosts(
            @RequestParam(required = false) @Pattern(regexp = PresentationValidationPatterns.ULID) final String lastId,
            @RequestParam(defaultValue = "10") @Min(1) @Max(20) final int size,
            @AuthenticationPrincipal final UserDetailsImpl userDetails) {

        // 自分の投稿一覧を取得する
        final List<PostSummary> postSummaries =
                postQueryService.listMyPosts(userDetails.getUserId(), toPostId(lastId), size);

        // レスポンス形式に変換する
        final List<PostItemResponse> responses = postResponseMapper.fromList(postSummaries);

        return ResponseEntity.ok(responses);
    }

    /**
     * 自分の返信一覧を取得します。
     *
     * @param lastId      最後の返信 ID
     * @param size        取得する返信数
     * @param userDetails 認証済みユーザーの詳細情報
     * @return 自分の返信一覧のレスポンス
     */
    @GetMapping("/me/replies")
    public ResponseEntity<List<ReplyItemResponse>> getMyReplies(
            @RequestParam(required = false) @Pattern(regexp = PresentationValidationPatterns.ULID) final String lastId,
            @RequestParam(defaultValue = "10") @Min(1) @Max(20) final int size,
            @AuthenticationPrincipal final UserDetailsImpl userDetails) {

        // 自分の返信一覧を取得する
        final List<ReplySummary> replySummaries =
                replyQueryService.listMyReplies(userDetails.getUserId(), toReplyId(lastId), size);

        // レスポンス形式に変換する
        final List<ReplyItemResponse> responses = replyResponseMapper.fromList(replySummaries);

        return ResponseEntity.ok(responses);
    }

    /**
     * ユーザー登録処理を行います。
     *
     * @param signUpRequest ユーザー登録リクエスト
     * @param request       HTTP リクエスト
     * @return ユーザー登録結果
     * @throws Exception ログイン処理等で発生しうる例外
     */
    @PostMapping
    public ResponseEntity<Void> create(
            @Valid @RequestBody final SignUpRequest signUpRequest,
            final HttpServletRequest request) throws Exception {

        // リクエスト情報をコマンド形式に変換する
        final EmailAddress email = EmailAddress.of(signUpRequest.getEmail());
        final RawPassword password = RawPassword.of(signUpRequest.getPassword());

        final CreateUserCommand command = CreateUserCommand.of(
                UserName.of(signUpRequest.getUserName()),
                email,
                password,
                BirthDate.of(signUpRequest.getBirthDate(), LocalDate.now(clock))
        );

        userCommandService.createUser(command);

        // 登録後、そのままログイン状態にする
        request.login(email.getValue(), password.getValue());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private PostId toPostId(final String rawPostId) {
        return rawPostId == null ? null : PostId.of(rawPostId);
    }

    private ReplyId toReplyId(final String rawReplyId) {
        return rawReplyId == null ? null : ReplyId.of(rawReplyId);
    }
}
