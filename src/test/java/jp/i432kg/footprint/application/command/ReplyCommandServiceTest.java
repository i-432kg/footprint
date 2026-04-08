package jp.i432kg.footprint.application.command;

import jp.i432kg.footprint.application.command.model.CreateReplyCommand;
import jp.i432kg.footprint.application.exception.resource.PostNotFoundException;
import jp.i432kg.footprint.application.exception.resource.ReplyNotFoundException;
import jp.i432kg.footprint.application.exception.resource.UserNotFoundException;
import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.repository.ReplyRepository;
import jp.i432kg.footprint.domain.service.PostDomainService;
import jp.i432kg.footprint.domain.service.ReplyDomainService;
import jp.i432kg.footprint.domain.service.UserDomainService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReplyCommandServiceTest {

    @Mock
    private ReplyRepository replyRepository;

    @Mock
    private PostDomainService postDomainService;

    @Mock
    private ReplyDomainService replyDomainService;

    @Mock
    private UserDomainService userDomainService;

    @Test
    void createReply_shouldThrowPostNotFoundException_whenPostDoesNotExist() {
        CreateReplyCommand command = CreateReplyCommand.of(
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                null,
                DomainTestFixtures.replyMessage()
        );
        when(postDomainService.isExistPost(DomainTestFixtures.postId())).thenReturn(false);
        ReplyCommandService service = new ReplyCommandService(
                replyRepository,
                postDomainService,
                replyDomainService,
                userDomainService
        );

        assertThatThrownBy(() -> service.createReply(command))
                .isInstanceOf(PostNotFoundException.class);

        verify(replyRepository, never()).saveReply(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void createReply_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        CreateReplyCommand command = CreateReplyCommand.of(
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                null,
                DomainTestFixtures.replyMessage()
        );
        when(postDomainService.isExistPost(DomainTestFixtures.postId())).thenReturn(true);
        when(userDomainService.isExistUser(DomainTestFixtures.userId())).thenReturn(false);
        ReplyCommandService service = new ReplyCommandService(
                replyRepository,
                postDomainService,
                replyDomainService,
                userDomainService
        );

        assertThatThrownBy(() -> service.createReply(command))
                .isInstanceOf(UserNotFoundException.class);

        verify(replyRepository, never()).saveReply(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void createReply_shouldThrowReplyNotFoundException_whenParentReplyDoesNotExist() {
        CreateReplyCommand command = CreateReplyCommand.of(
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                DomainTestFixtures.replyId(),
                DomainTestFixtures.replyMessage()
        );
        when(postDomainService.isExistPost(DomainTestFixtures.postId())).thenReturn(true);
        when(userDomainService.isExistUser(DomainTestFixtures.userId())).thenReturn(true);
        when(replyDomainService.findReplyById(DomainTestFixtures.replyId())).thenReturn(Optional.empty());
        ReplyCommandService service = new ReplyCommandService(
                replyRepository,
                postDomainService,
                replyDomainService,
                userDomainService
        );

        assertThatThrownBy(() -> service.createReply(command))
                .isInstanceOf(ReplyNotFoundException.class);

        verify(replyRepository, never()).saveReply(org.mockito.ArgumentMatchers.any());
    }
}
