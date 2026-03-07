package jp.i432kg.footprint.infrastructure.datasource.repository;

import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.repository.ReplyRepository;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.infrastructure.datasource.mapper.repository.ReplyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 返信に関するリポジトリの実装クラス
 */
@Repository
@RequiredArgsConstructor
public class ReplyRepositoryImpl implements ReplyRepository {

    private final ReplyMapper replyMapper;

    @Override
    public Optional<Reply> findReplyById(ReplyId replyId) {
        return replyMapper.findReplyById(replyId);
    }

    @Override
    public void saveReply(final Reply reply) {

        // 返信レコードを保存する
        final ReplyMapper.ReplyInsertEntity insertReplyEntity = ReplyMapper.ReplyInsertEntity.from(reply);
        replyMapper.insert(insertReplyEntity);
    }

    @Override
    public void increaseReplyCount(final ReplyId replyId) {
        replyMapper.incrementChildCount(replyId);
    }

}
