package jp.i432kg.footprint.application.command;

import jp.i432kg.footprint.application.command.model.CreateReplyCommand;
import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.repository.ReplyRepository;
import jp.i432kg.footprint.domain.service.ReplyDomainService;
import lombok.RequiredArgsConstructor;
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

    private final ReplyDomainService replyDomainService;

    /**
     * 指定された投稿または返信に対して、新しい返信を作成します。
     * 返信の保存と、親返信が存在する場合のカウントアップを同一トランザクション内で実行します。
     *
     * @param command 返信作成に必要な情報を含むコマンドオブジェクト
     */
    @Transactional
    public void createReply(final CreateReplyCommand command){

        // 返信作成時のバリデーション
        try {
            replyDomainService.isValidCreateReply(command.getPostId(), command.getUserId(), command.getParentReplyId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Reply ドメインモデルを構築し、DBに永続化する
        final Reply reply = Reply.of(
                command.getPostId(),
                command.getUserId(),
                command.getParentReplyId(),
                command.getMessage(),
                LocalDateTime.now()
        );
        replyRepository.saveReply(reply);

        // 親返信の子返信カウントを増やす
        if(command.hasParentReply()){
            replyRepository.increaseReplyCount(command.getParentReplyId());
        }
    }

}
