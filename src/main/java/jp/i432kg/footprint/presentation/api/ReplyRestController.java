package jp.i432kg.footprint.presentation.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jp.i432kg.footprint.application.command.ReplyCommandService;
import jp.i432kg.footprint.application.command.model.CreateReplyCommand;
import jp.i432kg.footprint.application.query.ReplyQueryService;
import jp.i432kg.footprint.application.query.model.ReplySummary;
import jp.i432kg.footprint.domain.model.ParentReply;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.domain.value.ReplyComment;
import jp.i432kg.footprint.infrastructure.security.UserDetailsImpl;
import jp.i432kg.footprint.presentation.api.request.ReplyRequest;
import jp.i432kg.footprint.presentation.api.response.ReplyItemResponse;
import jp.i432kg.footprint.presentation.api.response.mapper.ReplyResponseMapper;
import jp.i432kg.footprint.presentation.validation.PresentationValidationPatterns;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 返信（コメント）に関する操作を提供する API コントローラー
 */
@RestController
@RequestMapping("/api/replies")
@Validated
@RequiredArgsConstructor
public class ReplyRestController {

    private final ReplyCommandService replyCommandService;

    private final ReplyQueryService replyQueryService;

    private final ReplyResponseMapper replyResponseMapper;

    /**
     * 指定された親返信IDに紐づく、ネストされた返信一覧を取得します。
     *
     * @param parentReplyId 親返信の識別子
     * @return 返信アイテムのリスト
     */
    @GetMapping("/{parentReplyId}")
    public ResponseEntity<List<ReplyItemResponse>> getNextReplies(
            @PathVariable @Pattern(regexp = PresentationValidationPatterns.ULID) final String parentReplyId
    ) {

        // 返信一覧を取得する
        final List<ReplySummary> replySummaries = replyQueryService.listNestedReplies(ReplyId.of(parentReplyId));

        // レスポンス形式に変換する
        final List<ReplyItemResponse> responses = replyResponseMapper.fromList(replySummaries);

        return ResponseEntity.ok(responses);
    }

    /**
     * 返信作成処理を行います。
     *
     * @param postId 投稿の識別子
     * @param request 返信作成リクエスト
     * @param userDetails 認証済みユーザーの詳細情報
     * @return 返信作成結果
     */
    @PostMapping("/{postId}/reply")
    public ResponseEntity<Void> reply(
            @PathVariable @Pattern(regexp = PresentationValidationPatterns.ULID) final String postId,
            @Valid @RequestBody final ReplyRequest request,
            @AuthenticationPrincipal final UserDetailsImpl userDetails) {

        final CreateReplyCommand command = CreateReplyCommand.of(
                PostId.of(postId),
                userDetails.getUserId(),
                Optional.ofNullable(request.getParentReplyId())
                        .map(ReplyId::of)
                        .map(ParentReply::of)
                        .orElseGet(ParentReply::root),
                ReplyComment.of(request.getMessage())
        );

        replyCommandService.createReply(command);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
