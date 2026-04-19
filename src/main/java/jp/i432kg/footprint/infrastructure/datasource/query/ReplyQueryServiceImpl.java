package jp.i432kg.footprint.infrastructure.datasource.query;

import jp.i432kg.footprint.application.query.service.ReplyQueryService;
import jp.i432kg.footprint.application.query.model.ReplySummary;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.infrastructure.datasource.mapper.query.ReplyQueryMapper;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * {@link ReplyQueryService} のデータソース参照実装。
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyQueryServiceImpl implements ReplyQueryService {

    private final ReplyQueryMapper replyQueryMapper;

    @Override
    public List<ReplySummary> listTopLevelReplies(final PostId postId) {
        return replyQueryMapper.findTopLevelRepliesByPostId(postId);
    }

    @Override
    public List<ReplySummary> listNestedReplies(final ReplyId parentReplyId) {
        return replyQueryMapper.findNestedRepliesByParentId(parentReplyId);
    }

    @Override
    public List<ReplySummary> listMyReplies(final UserId userId, final @Nullable ReplyId lastId, final int size) {
        return replyQueryMapper.findMyReplies(userId, lastId, size);
    }
}
