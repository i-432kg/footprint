package jp.i432kg.footprint.application.command;

import com.github.f4b6a3.ulid.UlidCreator;
import jp.i432kg.footprint.application.command.model.CreateReplyCommand;
import jp.i432kg.footprint.application.exception.resource.PostNotFoundException;
import jp.i432kg.footprint.application.exception.resource.ReplyNotFoundException;
import jp.i432kg.footprint.application.exception.resource.UserNotFoundException;
import jp.i432kg.footprint.application.exception.usecase.ReplyCommandFailedException;
import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.repository.ReplyRepository;
import jp.i432kg.footprint.domain.service.PostDomainService;
import jp.i432kg.footprint.domain.service.ReplyDomainService;
import jp.i432kg.footprint.domain.service.UserDomainService;
import jp.i432kg.footprint.domain.value.ReplyId;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 返信（コメント）に関するユースケースを実行するアプリケーションサービス。
 */
@Service
@RequiredArgsConstructor
public class ReplyCommandService {

    private final ReplyRepository replyRepository;

    private final PostDomainService postDomainService;

    private final ReplyDomainService replyDomainService;

    private final UserDomainService userDomainService;

    /**
     * 指定された投稿または返信に対して、新しい返信を作成します。
     * 返信の保存と、親返信が存在する場合のカウントアップを同一トランザクション内で実行します。
     *
     * @param command 返信作成に必要な情報を含むコマンドオブジェクト
     */
    @Transactional
    public void createReply(final CreateReplyCommand command){

        // 返信先の投稿が存在するか確認
        if (!postDomainService.isExistPost(command.getPostId())) {
            throw new PostNotFoundException(command.getPostId());
        }

        // 返信者が存在するか確認
        if (!userDomainService.isExistUser(command.getUserId())) {
            throw new UserNotFoundException(command.getUserId());
        }

        // 親返信がある場合、それが同じ投稿に属しているか確認
        if (command.getParentReply().hasParent()) {
            final ReplyId parentReplyId = command.getParentReply().getReplyId();
            final Reply parentReply = replyDomainService.findReplyById(parentReplyId)
                    .orElseThrow(() -> new ReplyNotFoundException(parentReplyId));
            replyDomainService.validateParentReplyBelongsToPost(command.getPostId(), parentReply);
        }

        // ReplyId を生成 (ULID)
        final ReplyId replyId = ReplyId.of(UlidCreator.getUlid().toString());

        // Reply ドメインモデルを構築し、DBに永続化する
        final Reply reply = Reply.of(
                replyId,
                command.getPostId(),
                command.getUserId(),
                command.getParentReply(),
                command.getMessage(),
                LocalDateTime.now()
        );

        try {
            replyRepository.saveReply(reply);
        } catch (DataAccessException e) {
            throw ReplyCommandFailedException.saveFailed(replyId.value(), e);
        }

        // 親返信の子返信カウントを増やす
        if (command.getParentReply().hasParent()) {
            final ReplyId parentReplyId = command.getParentReply().getReplyId();
            try {
                replyRepository.increaseReplyCount(parentReplyId);
            } catch (DataAccessException e) {
                throw ReplyCommandFailedException.increaseReplyCountFailed(
                        parentReplyId.value(),
                        e
                );
            }
        }
    }

}
