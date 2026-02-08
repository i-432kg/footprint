package jp.i432kg.footprint.presentation.api;

import jp.i432kg.footprint.application.service.PostApplicationService;
import jp.i432kg.footprint.application.service.ReplyApplicationService;
import jp.i432kg.footprint.infrastructure.datasource.impl.UserDetailsImpl;
import jp.i432kg.footprint.presentation.api.dto.PostResponse;
import jp.i432kg.footprint.presentation.api.dto.ReplyResponse;
import jp.i432kg.footprint.presentation.helper.ImageUrlConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final PostApplicationService postApplicationService;

    private final ReplyApplicationService replyApplicationService;

    private final ImageUrlConverter imageUrlConverter;

    @GetMapping("/me/posts")
    public ResponseEntity<List<PostResponse>> getMyPosts(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        final List<PostResponse> responses = postApplicationService.getMyPosts(userDetails.getUserId())
                .asList().stream()
                .map(post -> new PostResponse(post, imageUrlConverter.convert(post.getImageFileName())))
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
