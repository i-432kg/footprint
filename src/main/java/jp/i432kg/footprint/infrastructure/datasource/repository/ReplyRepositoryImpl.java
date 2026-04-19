package jp.i432kg.footprint.infrastructure.datasource.repository;

import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.repository.ReplyRepository;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.infrastructure.datasource.mapper.repository.ReplyMapper;
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
            log.error("Failed to find reply by id. replyId={}", replyId.getValue(), e);
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
            log.error(
                    "Failed to save reply. replyId={}, postId={}, userId={}",
                    reply.getReplyId().getValue(),
                    reply.getPostId().getValue(),
                    reply.getUserId().getValue(),
                    e
            );
            throw e;
        }
    }

    @Override
    public void increaseReplyCount(final ReplyId replyId) {
        try {
            replyMapper.incrementChildCount(replyId);
        } catch (RuntimeException e) {
            log.error("Failed to increase reply count. replyId={}", replyId.getValue(), e);
            throw e;
        }
    }
}
