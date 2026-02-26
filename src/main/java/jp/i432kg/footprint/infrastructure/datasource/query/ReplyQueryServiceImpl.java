package jp.i432kg.footprint.infrastructure.datasource.query;

import jp.i432kg.footprint.application.query.ReplyQueryService;
import jp.i432kg.footprint.application.query.model.ReplySummary;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.infrastructure.datasource.mapper.query.ReplyQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 返信の参照サービス実装クラス
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
}
