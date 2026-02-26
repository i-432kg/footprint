package jp.i432kg.footprint.application.service;

import jp.i432kg.footprint.domain.model.Replies;
import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.repository.ReplyRepository;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReplyApplicationService {

    private final ReplyRepository replyRepository;

    @Transactional
    public void createReply(final Reply.NewReply newReply){

        // 返信を保存する
        replyRepository.saveReply(newReply);

        // 親返信の子返信カウントを増やす
        if(newReply.hasParentReply()){
            replyRepository.increaseReplyCount(newReply.getParentReplyId());
        }
    }

    @Transactional(readOnly = true)
    public Replies getMyReplies(final UserId userId){
        return replyRepository.findMyReplies(userId);
    }

}
