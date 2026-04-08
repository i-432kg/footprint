package jp.i432kg.footprint.domain.service;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.exception.ReplyPostMismatchException;
import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.repository.ReplyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReplyDomainServiceTest {

    @Mock
    private ReplyRepository replyRepository;
    @Test
    void findReplyById_shouldReturnEmpty_whenReplyIdIsNull() {
        ReplyDomainService service = new ReplyDomainService(replyRepository);

        assertThat(service.findReplyById(null)).isEmpty();
    }

    @Test
    void findReplyById_shouldReturnRepositoryResult_whenReplyIdExists() {
        when(replyRepository.findReplyById(DomainTestFixtures.replyId())).thenReturn(Optional.of(DomainTestFixtures.reply()));
        ReplyDomainService service = new ReplyDomainService(replyRepository);

        assertThat(service.findReplyById(DomainTestFixtures.replyId())).contains(DomainTestFixtures.reply());
    }

    @Test
    void findReplyById_shouldReturnEmpty_whenReplyDoesNotExist() {
        when(replyRepository.findReplyById(DomainTestFixtures.replyId())).thenReturn(Optional.empty());
        ReplyDomainService service = new ReplyDomainService(replyRepository);

        assertThat(service.findReplyById(DomainTestFixtures.replyId())).isEmpty();
    }

    @Test
    void validateParentReplyBelongsToPost_shouldThrowReplyPostMismatchException_whenParentReplyBelongsToAnotherPost() {
        Reply parentReply = Reply.of(
                DomainTestFixtures.replyId(),
                DomainTestFixtures.otherPostId(),
                DomainTestFixtures.userId(),
                null,
                DomainTestFixtures.replyMessage(),
                LocalDateTime.of(2026, 4, 1, 14, 0)
        );
        ReplyDomainService service = new ReplyDomainService(replyRepository);

        assertThatThrownBy(() -> service.validateParentReplyBelongsToPost(DomainTestFixtures.postId(), parentReply))
                .isInstanceOf(ReplyPostMismatchException.class);
    }

    @Test
    void validateParentReplyBelongsToPost_shouldDoNothing_whenParentReplyBelongsToSamePost() {
        ReplyDomainService service = new ReplyDomainService(replyRepository);

        assertThatCode(() -> service.validateParentReplyBelongsToPost(
                DomainTestFixtures.postId(),
                DomainTestFixtures.reply()
        )).doesNotThrowAnyException();
    }
}
