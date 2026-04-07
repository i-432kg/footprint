package jp.i432kg.footprint.domain.service;

import jp.i432kg.footprint.application.exception.resource.PostNotFoundException;
import jp.i432kg.footprint.application.exception.resource.ReplyNotFoundException;
import jp.i432kg.footprint.application.exception.resource.UserNotFoundException;
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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReplyDomainServiceTest {

    @Mock
    private ReplyRepository replyRepository;

    @Mock
    private PostDomainService postDomainService;

    @Mock
    private UserDomainService userDomainService;

    @Test
    void validateCreateReply_shouldDoNothing_whenPostAndUserExistAndNoParentReply() {
        when(postDomainService.isExistPost(DomainTestFixtures.postId())).thenReturn(true);
        when(userDomainService.isExistUser(DomainTestFixtures.userId())).thenReturn(true);
        ReplyDomainService service = new ReplyDomainService(replyRepository, postDomainService, userDomainService);

        assertThatCode(() -> service.validateCreateReply(
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                null
        )).doesNotThrowAnyException();
    }

    @Test
    void validateCreateReply_shouldThrowPostNotFoundException_whenPostDoesNotExist() {
        when(postDomainService.isExistPost(DomainTestFixtures.postId())).thenReturn(false);
        ReplyDomainService service = new ReplyDomainService(replyRepository, postDomainService, userDomainService);

        assertThatThrownBy(() -> service.validateCreateReply(
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                null
        )).isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void validateCreateReply_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        when(postDomainService.isExistPost(DomainTestFixtures.postId())).thenReturn(true);
        when(userDomainService.isExistUser(DomainTestFixtures.userId())).thenReturn(false);
        ReplyDomainService service = new ReplyDomainService(replyRepository, postDomainService, userDomainService);

        assertThatThrownBy(() -> service.validateCreateReply(
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                null
        )).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void validateCreateReply_shouldThrowReplyNotFoundException_whenParentReplyDoesNotExist() {
        when(postDomainService.isExistPost(DomainTestFixtures.postId())).thenReturn(true);
        when(userDomainService.isExistUser(DomainTestFixtures.userId())).thenReturn(true);
        when(replyRepository.findReplyById(DomainTestFixtures.replyId())).thenReturn(Optional.empty());
        ReplyDomainService service = new ReplyDomainService(replyRepository, postDomainService, userDomainService);

        assertThatThrownBy(() -> service.validateCreateReply(
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                DomainTestFixtures.replyId()
        )).isInstanceOf(ReplyNotFoundException.class);
    }

    @Test
    void validateCreateReply_shouldThrowReplyPostMismatchException_whenParentReplyBelongsToAnotherPost() {
        Reply parentReply = Reply.of(
                DomainTestFixtures.replyId(),
                DomainTestFixtures.otherPostId(),
                DomainTestFixtures.userId(),
                null,
                DomainTestFixtures.replyMessage(),
                LocalDateTime.of(2026, 4, 1, 14, 0)
        );

        when(postDomainService.isExistPost(DomainTestFixtures.postId())).thenReturn(true);
        when(userDomainService.isExistUser(DomainTestFixtures.userId())).thenReturn(true);
        when(replyRepository.findReplyById(DomainTestFixtures.replyId())).thenReturn(Optional.of(parentReply));
        ReplyDomainService service = new ReplyDomainService(replyRepository, postDomainService, userDomainService);

        assertThatThrownBy(() -> service.validateCreateReply(
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                DomainTestFixtures.replyId()
        )).isInstanceOf(ReplyPostMismatchException.class);
    }

    @Test
    void validateCreateReply_shouldDoNothing_whenParentReplyBelongsToSamePost() {
        when(postDomainService.isExistPost(DomainTestFixtures.postId())).thenReturn(true);
        when(userDomainService.isExistUser(DomainTestFixtures.userId())).thenReturn(true);
        when(replyRepository.findReplyById(DomainTestFixtures.replyId())).thenReturn(Optional.of(DomainTestFixtures.reply()));
        ReplyDomainService service = new ReplyDomainService(replyRepository, postDomainService, userDomainService);

        assertThatCode(() -> service.validateCreateReply(
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                DomainTestFixtures.replyId()
        )).doesNotThrowAnyException();
    }
}
