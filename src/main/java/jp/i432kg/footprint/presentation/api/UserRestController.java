package jp.i432kg.footprint.presentation.api;

import jp.i432kg.footprint.application.query.UserQueryService;
import jp.i432kg.footprint.application.query.model.UserProfileSummary;
import jp.i432kg.footprint.application.service.PostApplicationService;
import jp.i432kg.footprint.application.service.ReplyApplicationService;
import jp.i432kg.footprint.infrastructure.datasource.impl.UserDetailsImpl;
import jp.i432kg.footprint.presentation.api.dto.PostResponse;
import jp.i432kg.footprint.presentation.api.dto.ReplyResponse;
import jp.i432kg.footprint.presentation.api.response.UserProfileItemResponse;
import jp.i432kg.footprint.presentation.api.response.mapper.UserProfileResponseMapper;
import jp.i432kg.footprint.presentation.helper.ImageUrlConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ユーザーに関する情報を管理する API コントローラー
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final PostApplicationService postApplicationService;

    private final ReplyApplicationService replyApplicationService;

    private final UserQueryService userQueryService;

    private final UserProfileResponseMapper userProfileResponseMapper;

    private final ImageUrlConverter imageUrlConverter;

    /**
     * 現在ログインしているユーザーのプロフィール情報を取得します。
     *
     * @param userDetails 認証済みユーザーの詳細情報
     * @return ユーザープロフィールのレスポンス
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileItemResponse> getCurrentUser_v2(@AuthenticationPrincipal final UserDetailsImpl userDetails) {

        // ユーザーのプロフィール情報を取得する
        final UserProfileSummary userProfile = userQueryService.getUserProfile(userDetails.getUserId());

        // レスポンス形式に変換する
        final UserProfileItemResponse response = userProfileResponseMapper.from(userProfile);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/posts")
    public ResponseEntity<List<PostResponse>> getMyPosts(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        final List<PostResponse> responses = postApplicationService.getMyPosts(userDetails.getUserId())
                .asList().stream()
                .map(post -> new PostResponse(post, imageUrlConverter.convert(post.getImage().getFilePath())))
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/me/replies")
    public ResponseEntity<List<ReplyResponse>> getMyReplies(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        final List<ReplyResponse> responses = replyApplicationService.getMyReplies(userDetails.getUserId())
                .asList().stream()
                .map(ReplyResponse::new)
                .toList();

        return ResponseEntity.ok(responses);
    }
}
