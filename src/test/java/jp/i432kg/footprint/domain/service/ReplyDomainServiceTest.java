package jp.i432kg.footprint.domain.service;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.exception.ReplyPostMismatchException;
import jp.i432kg.footprint.domain.model.ParentReply;
import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.repository.ReplyRepository;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.domain.value.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReplyDomainServiceTest {

    @Mock
    private ReplyRepository replyRepository;

    @Test
    @DisplayName("ReplyDomainService.findReplyById は repository の検索結果をそのまま返す")
    void should_returnReplyOptional_when_replyIdIsGiven() {
        final ReplyId replyId = DomainTestFixtures.replyId();
        final Reply reply = DomainTestFixtures.reply();
        final ReplyDomainService service = new ReplyDomainService(replyRepository);
        when(replyRepository.findReplyById(replyId)).thenReturn(Optional.of(reply));

        final Optional<Reply> actual = service.findReplyById(replyId);

        assertThat(actual).contains(reply);
        verify(replyRepository).findReplyById(replyId);
    }

    @Test
    @DisplayName("ReplyDomainService.findReplyById は返信が存在しない場合に空を返す")
    void should_returnEmpty_when_replyDoesNotExist() {
        final ReplyId replyId = DomainTestFixtures.replyId();
        final ReplyDomainService service = new ReplyDomainService(replyRepository);
        when(replyRepository.findReplyById(replyId)).thenReturn(Optional.empty());

        final Optional<Reply> actual = service.findReplyById(replyId);

        assertThat(actual).isEmpty();
        verify(replyRepository).findReplyById(replyId);
    }

    @Test
    @DisplayName("ReplyDomainService.findReplyById は replyId が null の場合に空を返し repository を呼ばない")
    void should_returnEmptyAndNotCallRepository_when_replyIdIsNull() {
        final ReplyDomainService service = new ReplyDomainService(replyRepository);

        final Optional<Reply> actual = service.findReplyById(null);

        assertThat(actual).isEmpty();
        verifyNoInteractions(replyRepository);
    }

    @Test
    @DisplayName("ReplyDomainService.validateParentReplyBelongsToPost は親返信が同じ投稿に属する場合に例外を送出しない")
    void should_notThrowException_when_parentReplyBelongsToSamePost() {
        final ReplyDomainService service = new ReplyDomainService(replyRepository);
        final PostId postId = DomainTestFixtures.postId();
        final Reply parentReply = DomainTestFixtures.reply();

        assertThatCode(() -> service.validateParentReplyBelongsToPost(postId, parentReply))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("ReplyDomainService.validateParentReplyBelongsToPost は親返信が別投稿に属する場合に不一致例外を送出する")
    void should_throwReplyPostMismatchException_when_parentReplyBelongsToDifferentPost() {
        final ReplyDomainService service = new ReplyDomainService(replyRepository);
        final PostId actualPostId = DomainTestFixtures.postId();
        final PostId expectedPostId = DomainTestFixtures.otherPostId();
        final Reply parentReply = replyBelongingTo(expectedPostId);

        assertThatThrownBy(() -> service.validateParentReplyBelongsToPost(actualPostId, parentReply))
                .isInstanceOf(ReplyPostMismatchException.class)
                .hasMessageContaining("expectedPostId=" + expectedPostId)
                .hasMessageContaining("actualPostId=" + actualPostId);
    }

    private static Reply replyBelongingTo(final PostId postId) {
        final ReplyId replyId = DomainTestFixtures.replyId();
        final UserId userId = DomainTestFixtures.userId();
        return Reply.of(
                replyId,
                postId,
                userId,
                ParentReply.root(),
                DomainTestFixtures.replyMessage(),
                LocalDateTime.of(2026, 4, 1, 13, 30)
        );
    }
}
