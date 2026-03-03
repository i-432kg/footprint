package jp.i432kg.footprint.presentation.api;

import jakarta.servlet.http.HttpServletRequest;
import jp.i432kg.footprint.application.command.model.CreateUserCommand;
import jp.i432kg.footprint.application.query.PostQueryService;
import jp.i432kg.footprint.application.query.ReplyQueryService;
import jp.i432kg.footprint.application.query.UserQueryService;
import jp.i432kg.footprint.application.query.model.PostSummary;
import jp.i432kg.footprint.application.query.model.ReplySummary;
import jp.i432kg.footprint.application.query.model.UserProfileSummary;
import jp.i432kg.footprint.application.command.UserCommandService;
import jp.i432kg.footprint.domain.value.*;
import jp.i432kg.footprint.infrastructure.security.UserDetailsImpl;
import jp.i432kg.footprint.presentation.api.request.SignUpRequest;
import jp.i432kg.footprint.presentation.api.response.PostItemResponse;
import jp.i432kg.footprint.presentation.api.response.ReplyItemResponse;
import jp.i432kg.footprint.presentation.api.response.UserProfileItemResponse;
import jp.i432kg.footprint.presentation.api.response.mapper.PostResponseMapper;
import jp.i432kg.footprint.presentation.api.response.mapper.ReplyResponseMapper;
import jp.i432kg.footprint.presentation.api.response.mapper.UserProfileResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ユーザーに関する情報を管理する API コントローラー
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final PostQueryService postQueryService;

    private final ReplyQueryService replyQueryService;

    private final UserQueryService userQueryService;

    private final UserCommandService userCommandService;

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
     * @param lastId 最後の投稿 ID
     * @param size 取得する投稿数
     * @param userDetails 認証済みユーザーの詳細情報
     * @return 自分の投稿一覧のレスポンス
     */
    @GetMapping("/me/posts")
    public ResponseEntity<List<PostItemResponse>> getMyPosts(
            @RequestParam(required = false) final PostId lastId,
            @RequestParam(defaultValue = "10") final int size,
            @AuthenticationPrincipal final UserDetailsImpl userDetails) {

        // 自分の投稿一覧を取得する
        List<PostSummary> postSummaries =
                postQueryService.listMyPosts(userDetails.getUserId(), lastId, size);

        // レスポンス形式に変換する
        List<PostItemResponse> responses = postResponseMapper.fromList(postSummaries);

        return ResponseEntity.ok(responses);
    }

    /**
     * 自分の返信一覧を取得します。
     *
     * @param lastId 最後の返信 ID
     * @param size 取得する返信数
     * @param userDetails 認証済みユーザーの詳細情報
     * @return 自分の返信一覧のレスポンス
     */
    @GetMapping("/me/replies")
    public ResponseEntity<List<ReplyItemResponse>> getMyReplies(
            @RequestParam(required = false) final ReplyId lastId,
            @RequestParam(defaultValue = "10") final int size,
            @AuthenticationPrincipal final UserDetailsImpl userDetails) {

        // 自分の返信一覧を取得する
        final List<ReplySummary> replySummaries =
                replyQueryService.listMyReplies(userDetails.getUserId(), lastId, size);

        // レスポンス形式に変換する
        final List<ReplyItemResponse> responses = replyResponseMapper.fromList(replySummaries);

        return ResponseEntity.ok(responses);
    }

    /**
     * ユーザー登録処理を行います。
     *
     * @param signUpRequest ユーザー登録リクエスト
     * @param result バリデーション結果
     * @param request HTTP リクエスト
     * @return ユーザー登録結果
     */
    @PostMapping
    public ResponseEntity<?> create(
            @Validated @RequestBody final SignUpRequest signUpRequest,
            final BindingResult result,
            final HttpServletRequest request) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        // リクエスト情報をコマンド形式に変換する
        final CreateUserCommand command = CreateUserCommand.of(
                UserName.of(signUpRequest.getUserName()),
                EmailAddress.of(signUpRequest.getEmail()),
                RawPassword.of(signUpRequest.getPassword()),
                BirthDate.of(signUpRequest.getBirthDate())
        );

        try {
            userCommandService.createUser(command);

            // 登録後、そのままログイン状態にする
            request.login(signUpRequest.getEmail(), signUpRequest.getPassword());

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("ユーザー登録に失敗しました");
        }
    }
}
