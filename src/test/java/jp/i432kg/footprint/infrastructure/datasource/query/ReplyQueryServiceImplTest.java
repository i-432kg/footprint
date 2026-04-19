package jp.i432kg.footprint.infrastructure.datasource.query;

import jp.i432kg.footprint.application.query.model.ReplySummary;
import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.infrastructure.datasource.mapper.query.ReplyQueryMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReplyQueryServiceImplTest {

    @Mock
    private ReplyQueryMapper replyQueryMapper;

    @Test
    @DisplayName("ReplyQueryServiceImpl.listTopLevelReplies はトップレベル返信一覧を返す")
    void should_returnTopLevelReplies_when_listTopLevelRepliesCalled() {
        final PostId postId = DomainTestFixtures.postId();
        final List<ReplySummary> expected = List.of(replySummary("reply-01", null));
        when(replyQueryMapper.findTopLevelRepliesByPostId(postId)).thenReturn(expected);

        final List<ReplySummary> actual = newService().listTopLevelReplies(postId);

        assertThat(actual).isEqualTo(expected);
        verify(replyQueryMapper).findTopLevelRepliesByPostId(postId);
        verifyNoMoreInteractions(replyQueryMapper);
    }

    @Test
    @DisplayName("ReplyQueryServiceImpl.listNestedReplies は子返信一覧を返す")
    void should_returnNestedReplies_when_listNestedRepliesCalled() {
        final ReplyId parentReplyId = DomainTestFixtures.replyId();
        final List<ReplySummary> expected = List.of(replySummary("reply-02", parentReplyId.getValue()));
        when(replyQueryMapper.findNestedRepliesByParentId(parentReplyId)).thenReturn(expected);

        final List<ReplySummary> actual = newService().listNestedReplies(parentReplyId);

        assertThat(actual).isEqualTo(expected);
        verify(replyQueryMapper).findNestedRepliesByParentId(parentReplyId);
        verifyNoMoreInteractions(replyQueryMapper);
    }

    @Test
    @DisplayName("ReplyQueryServiceImpl.listMyReplies は自分の返信一覧を返す")
    void should_returnMyReplies_when_listMyRepliesCalled() {
        final UserId userId = DomainTestFixtures.userId();
        final ReplyId lastId = DomainTestFixtures.replyId();
        final List<ReplySummary> expected = List.of(replySummary("reply-01", null));
        when(replyQueryMapper.findMyReplies(userId, lastId, 10)).thenReturn(expected);

        final List<ReplySummary> actual = newService().listMyReplies(userId, lastId, 10);

        assertThat(actual).isEqualTo(expected);
        verify(replyQueryMapper).findMyReplies(userId, lastId, 10);
        verifyNoMoreInteractions(replyQueryMapper);
    }

    @Test
    @DisplayName("ReplyQueryServiceImpl.listMyReplies は lastId が null の場合もそのまま mapper へ委譲する")
    void should_delegateNullLastId_when_listMyRepliesCalledWithoutPagingCursor() {
        final UserId userId = DomainTestFixtures.userId();
        final List<ReplySummary> expected = List.of();
        when(replyQueryMapper.findMyReplies(userId, null, 10)).thenReturn(expected);

        final List<ReplySummary> actual = newService().listMyReplies(userId, null, 10);

        assertThat(actual).isEqualTo(expected);
        verify(replyQueryMapper).findMyReplies(userId, null, 10);
        verifyNoMoreInteractions(replyQueryMapper);
    }

    private ReplyQueryServiceImpl newService() {
        return new ReplyQueryServiceImpl(replyQueryMapper);
    }

    private static ReplySummary replySummary(final String id, final String parentReplyId) {
        return new ReplySummary(
                id,
                DomainTestFixtures.POST_ID,
                DomainTestFixtures.USER_ID,
                parentReplyId,
                "reply",
                0,
                LocalDateTime.of(2026, 4, 19, 12, 30)
        );
    }
}
