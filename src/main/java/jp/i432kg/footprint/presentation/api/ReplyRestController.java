package jp.i432kg.footprint.presentation.api;

import jp.i432kg.footprint.application.service.ReplyApplicationService;
import jp.i432kg.footprint.domain.model.Replies;
import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.value.Comment;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.infrastructure.datasource.impl.UserDetailsImpl;
import jp.i432kg.footprint.presentation.api.dto.ReplyRequest;
import jp.i432kg.footprint.presentation.api.dto.ReplyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ReplyRestController {

    private final ReplyApplicationService replyApplicationService;

    /**
     * 指定された投稿IDに紐づく1階層目のコメントを取得します。
     *
     * @param postId コメントを取得したい投稿の識別子
     * @return コメントのリスト
     */
    @GetMapping("/api/post/{postId}/replies")
    public List<ReplyResponse> getReplies(@PathVariable final PostId postId) {

        final Replies replies = replyApplicationService.getRootReplies(postId);
        return replies.asList().stream()
                .map(ReplyResponse::new)
                .toList();
    }

    /**
     * 指定された親コメントIDに紐づく子コメントを取得します。
     *
     * @param parentReplyId 親コメントの識別子
     * @return 子コメントのリスト
     */
    @GetMapping("/api/reply/{parentReplyId}/replies")
    public List<ReplyResponse> getNextReplies(@PathVariable final ReplyId parentReplyId) {

        final Replies replies = replyApplicationService.getNextReplies(parentReplyId);
        return replies.asList().stream()
                .map(ReplyResponse::new)
                .toList();
    }


    /**
     * 指定された投稿にコメントを作成します。
     *
     * @param postId 投稿の識別子
     * @param request コメント作成リクエスト情報を含むオブジェクト
     * @param userDetails 認証済みユーザーの詳細情報
     * @return リダイレクト先のURL
     */
    @PostMapping("/api/post/{postId}/reply")
    public String reply(
            @PathVariable final PostId postId,
            @RequestBody final ReplyRequest request,
            @AuthenticationPrincipal final UserDetailsImpl userDetails) {

        final Reply.NewReply newReply = Reply.newReply()
                .postId(postId)
                .userId(userDetails.getUserId())
                .parentReplyId(Optional.ofNullable(request.getParentReplyId()).map(ReplyId::new).orElse(null))
                .content(new Comment(request.getContent()))
                .build();

        replyApplicationService.createReply(newReply);

        return "redirect:/";
    }

}
