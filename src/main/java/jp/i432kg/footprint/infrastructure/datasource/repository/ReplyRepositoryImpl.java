package jp.i432kg.footprint.infrastructure.datasource.repository;

import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.repository.ReplyRepository;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.infrastructure.datasource.mapper.repository.ReplyMapper;
import jp.i432kg.footprint.logging.LoggingEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * {@link ReplyRepository} のデータソース永続化実装。
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ReplyRepositoryImpl implements ReplyRepository {

    private final ReplyMapper replyMapper;

    @Override
    public Optional<Reply> findReplyById(final ReplyId replyId) {
        try {
            return replyMapper.findReplyById(replyId)
                    .map(ReplyMapper.ReplyResultEntity::toDomain);
        } catch (RuntimeException e) {
            log.atError()
                    .addKeyValue("event", LoggingEvents.REPLY_FIND_FAILED)
                    .addKeyValue("replyId", replyId.getValue())
                    .setCause(e)
                    .log("Failed to find reply");
            throw e;
        }
    }

    @Override
    public void saveReply(final Reply reply) {
        try {
            // 返信レコードを保存する
            final ReplyMapper.ReplyInsertEntity insertReplyEntity = ReplyMapper.ReplyInsertEntity.from(reply);
            replyMapper.insert(insertReplyEntity);
        } catch (RuntimeException e) {
            log.atError()
                    .addKeyValue("event", LoggingEvents.REPLY_SAVE_FAILED)
                    .addKeyValue("replyId", reply.getReplyId().getValue())
                    .addKeyValue("postId", reply.getPostId().getValue())
                    .addKeyValue("userId", reply.getUserId().getValue())
                    .setCause(e)
                    .log("Failed to save reply");
            throw e;
        }
    }

    @Override
    public void increaseReplyCount(final ReplyId replyId) {
        try {
            replyMapper.incrementChildCount(replyId);
        } catch (RuntimeException e) {
            log.atError()
                    .addKeyValue("event", LoggingEvents.REPLY_COUNT_INCREMENT_FAILED)
                    .addKeyValue("replyId", replyId.getValue())
                    .setCause(e)
                    .log("Failed to increase reply count");
            throw e;
        }
    }
}
