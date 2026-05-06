package jp.i432kg.footprint.infrastructure.datasource.repository;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.infrastructure.datasource.mapper.repository.ReplyMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReplyRepositoryImplTest {

    @Mock
    private ReplyMapper replyMapper;

    @Test
    @DisplayName("ReplyRepositoryImpl.findReplyById は検索結果を Reply へ変換して返す")
    void should_returnDomainReply_when_findReplyByIdFindsEntity() throws Exception {
        final ReplyId replyId = DomainTestFixtures.replyId();
        final ReplyMapper.ReplyResultEntity resultEntity = replyResultEntity(replyId);
        when(replyMapper.findReplyById(replyId)).thenReturn(Optional.of(resultEntity));

        final Optional<Reply> actual = newRepository().findReplyById(replyId);

        assertThat(actual).isPresent();
        assertThat(actual.orElseThrow().getReplyId()).isEqualTo(replyId);
        assertThat(actual.orElseThrow().getPostId()).isEqualTo(DomainTestFixtures.postId());
        assertThat(actual.orElseThrow().getUserId()).isEqualTo(DomainTestFixtures.userId());
        assertThat(actual.orElseThrow().getParentReplyId()).isEqualTo(DomainTestFixtures.otherReplyId());
        assertThat(actual.orElseThrow().getMessage()).isEqualTo(DomainTestFixtures.replyMessage());
        assertThat(actual.orElseThrow().getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 4, 19, 8, 30));
        verify(replyMapper).findReplyById(replyId);
        verifyNoMoreInteractions(replyMapper);
    }

    @Test
    @DisplayName("ReplyRepositoryImpl.findReplyById は返信が存在しない場合に Optional.empty を返す")
    void should_returnEmptyOptional_when_findReplyByIdFindsNothing() {
        final ReplyId replyId = DomainTestFixtures.replyId();
        when(replyMapper.findReplyById(replyId)).thenReturn(Optional.empty());

        final Optional<Reply> actual = newRepository().findReplyById(replyId);

        assertThat(actual).isEmpty();
        verify(replyMapper).findReplyById(replyId);
        verifyNoMoreInteractions(replyMapper);
    }

    @Test
    @DisplayName("ReplyRepositoryImpl.saveReply は返信を mapper へ保存委譲する")
    void should_saveReply_when_saveReplyCalled() {
        final Reply reply = DomainTestFixtures.reply();

        newRepository().saveReply(reply);

        final ArgumentCaptor<ReplyMapper.ReplyInsertEntity> captor =
                ArgumentCaptor.forClass(ReplyMapper.ReplyInsertEntity.class);
        verify(replyMapper).insert(captor.capture());
        verifyNoMoreInteractions(replyMapper);

        final ReplyMapper.ReplyInsertEntity actual = captor.getValue();
        assertThat(actual.getId()).isNull();
        assertThat(actual.getReplyId()).isEqualTo(reply.getReplyId());
        assertThat(actual.getPostId()).isEqualTo(reply.getPostId());
        assertThat(actual.getUserId()).isEqualTo(reply.getUserId());
        assertThat(actual.getParentReplyId()).isNull();
        assertThat(actual.getMessage()).isEqualTo(reply.getMessage());
        assertThat(actual.getChildCount()).isZero();
        assertThat(actual.getCreatedAt()).isEqualTo(reply.getCreatedAt());
        assertThat(actual.getUpdatedAt()).isEqualTo(reply.getCreatedAt());
    }

    @Test
    @DisplayName("ReplyRepositoryImpl.increaseReplyCount は子返信数更新を mapper へ委譲する")
    void should_incrementChildCount_when_increaseReplyCountCalled() {
        final ReplyId replyId = DomainTestFixtures.replyId();

        newRepository().increaseReplyCount(replyId);

        verify(replyMapper).incrementChildCount(replyId);
        verifyNoMoreInteractions(replyMapper);
    }

    @Test
    @DisplayName("ReplyRepositoryImpl.findReplyById は mapper 例外を再送出する")
    void should_rethrowException_when_findReplyByIdFails() {
        final ReplyId replyId = DomainTestFixtures.replyId();
        final RuntimeException expected = new RuntimeException("find failed");
        when(replyMapper.findReplyById(replyId)).thenThrow(expected);

        assertThatThrownBy(() -> newRepository().findReplyById(replyId))
                .isSameAs(expected);

        verify(replyMapper).findReplyById(replyId);
        verifyNoMoreInteractions(replyMapper);
    }

    @Test
    @DisplayName("ReplyRepositoryImpl.saveReply は mapper 例外を再送出する")
    void should_rethrowException_when_saveReplyFails() {
        final Reply reply = DomainTestFixtures.reply();
        final RuntimeException expected = new RuntimeException("insert failed");
        doThrow(expected).when(replyMapper).insert(org.mockito.ArgumentMatchers.any());

        assertThatThrownBy(() -> newRepository().saveReply(reply))
                .isSameAs(expected);

        verify(replyMapper).insert(org.mockito.ArgumentMatchers.any());
        verifyNoMoreInteractions(replyMapper);
    }

    @Test
    @DisplayName("ReplyRepositoryImpl.increaseReplyCount は mapper 例外を再送出する")
    void should_rethrowException_when_increaseReplyCountFails() {
        final ReplyId replyId = DomainTestFixtures.replyId();
        final RuntimeException expected = new RuntimeException("increment failed");
        doThrow(expected).when(replyMapper).incrementChildCount(replyId);

        assertThatThrownBy(() -> newRepository().increaseReplyCount(replyId))
                .isSameAs(expected);

        verify(replyMapper).incrementChildCount(replyId);
        verifyNoMoreInteractions(replyMapper);
    }

    private ReplyRepositoryImpl newRepository() {
        return new ReplyRepositoryImpl(replyMapper);
    }

    private static ReplyMapper.ReplyResultEntity replyResultEntity(final ReplyId replyId) throws Exception {
        final Constructor<ReplyMapper.ReplyResultEntity> constructor =
                ReplyMapper.ReplyResultEntity.class.getDeclaredConstructor(
                        jp.i432kg.footprint.domain.value.ReplyId.class,
                        jp.i432kg.footprint.domain.value.PostId.class,
                        jp.i432kg.footprint.domain.value.UserId.class,
                        jp.i432kg.footprint.domain.value.ReplyId.class,
                        jp.i432kg.footprint.domain.value.ReplyComment.class,
                        LocalDateTime.class
                );
        constructor.setAccessible(true);
        return constructor.newInstance(
                replyId,
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                DomainTestFixtures.otherReplyId(),
                DomainTestFixtures.replyMessage(),
                LocalDateTime.of(2026, 4, 19, 8, 30)
        );
    }
}
