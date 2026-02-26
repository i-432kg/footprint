package jp.i432kg.footprint.infrastructure.datasource.impl;

import jp.i432kg.footprint.domain.model.Replies;
import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.repository.ReplyRepository;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.infrastructure.datasource.mapper.ReplyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReplyRepositoryImpl implements ReplyRepository {

    private final ReplyMapper replyMapper;

//    @Override
//    public Replies findRootsByPostId(PostId postId) {
//        return Replies.of(replyMapper.findByPostIdAndParentReplyIdIsNull(postId));
//    }
//
//    @Override
//    public Replies findNextByParentReplyId(ReplyId parentReplyId) {
//        return Replies.of(replyMapper.findByParentReplyId(parentReplyId));
//    }

    @Override
    public void saveReply(Reply.NewReply newReply) {

        // 返信レコードを保存する
        final ReplyMapper.ReplyInsertEntity entity = ReplyMapper.ReplyInsertEntity.from(newReply);
        replyMapper.insert(entity);
    }

    @Override
    public void increaseReplyCount(ReplyId replyId) {
        replyMapper.incrementChildCount(replyId);
    }

    @Override
    public Replies findMyReplies(final UserId userId) {
        return Replies.of(replyMapper.findByUserId(userId));
    }


}
