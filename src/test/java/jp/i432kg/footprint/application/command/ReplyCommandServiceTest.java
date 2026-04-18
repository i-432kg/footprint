package jp.i432kg.footprint.application.command;

import jp.i432kg.footprint.application.command.model.CreateReplyCommand;
import jp.i432kg.footprint.application.exception.resource.PostNotFoundException;
import jp.i432kg.footprint.application.exception.resource.ReplyNotFoundException;
import jp.i432kg.footprint.application.exception.resource.UserNotFoundException;
import jp.i432kg.footprint.application.exception.usecase.ReplyCommandFailedException;
import jp.i432kg.footprint.application.port.ReplyIdGenerator;
import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.model.ParentReply;
import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.repository.ReplyRepository;
import jp.i432kg.footprint.domain.service.PostDomainService;
import jp.i432kg.footprint.domain.service.ReplyDomainService;
import jp.i432kg.footprint.domain.service.UserDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReplyCommandServiceTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-04-18T10:15:30Z"),
            ZoneId.of("Asia/Tokyo")
    );
    private static final String FIXED_REPLY_ID = "01ARZ3NDEKTSV4RRFFQ69G5FAZ";

    @Mock
    private ReplyRepository replyRepository;

    @Mock
    private PostDomainService postDomainService;

    @Mock
    private ReplyDomainService replyDomainService;

    @Mock
    private UserDomainService userDomainService;

    private ReplyCommandService newService() {
        return new ReplyCommandService(
                replyRepository,
                postDomainService,
                replyDomainService,
                userDomainService,
                FIXED_CLOCK,
                fixedReplyIdGenerator(FIXED_REPLY_ID)
        );
    }

    @Test
    @DisplayName("ReplyCommandService.createReply は親返信がない場合に返信を保存する")
    void should_createRootReply_when_parentReplyDoesNotExist() {
        final CreateReplyCommand command = CreateReplyCommand.of(
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                ParentReply.root(),
                DomainTestFixtures.replyMessage()
        );
        when(postDomainService.isExistPost(command.getPostId())).thenReturn(true);
        when(userDomainService.isExistUser(command.getUserId())).thenReturn(true);

        newService().createReply(command);

        final ArgumentCaptor<Reply> captor = ArgumentCaptor.forClass(Reply.class);
        verify(replyRepository).saveReply(captor.capture());
        final Reply actual = captor.getValue();

        assertThat(actual.getPostId()).isEqualTo(command.getPostId());
        assertThat(actual.getUserId()).isEqualTo(command.getUserId());
        assertThat(actual.getMessage()).isEqualTo(command.getMessage());
        assertThat(actual.getParentReply()).isEqualTo(command.getParentReply());
        assertThat(actual.hasParentReply()).isFalse();
        assertThat(actual.getReplyId().getValue()).isEqualTo(FIXED_REPLY_ID);
        assertThat(actual.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 4, 18, 19, 15, 30));
        verify(replyRepository, never()).increaseReplyCount(any());
    }

    @Test
    @DisplayName("ReplyCommandService.createReply は親返信がある場合に返信保存と返信数更新を行う")
    void should_createChildReply_when_parentReplyExists() {
        final ParentReply parentReply = ParentReply.of(DomainTestFixtures.replyId());
        final CreateReplyCommand command = CreateReplyCommand.of(
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                parentReply,
                DomainTestFixtures.replyMessage()
        );
        final Reply parent = DomainTestFixtures.reply();
        when(postDomainService.isExistPost(command.getPostId())).thenReturn(true);
        when(userDomainService.isExistUser(command.getUserId())).thenReturn(true);
        when(replyDomainService.findReplyById(DomainTestFixtures.replyId())).thenReturn(Optional.of(parent));

        newService().createReply(command);

        final ArgumentCaptor<Reply> captor = ArgumentCaptor.forClass(Reply.class);
        verify(replyRepository).saveReply(captor.capture());
        final Reply actual = captor.getValue();

        assertThat(actual.getPostId()).isEqualTo(command.getPostId());
        assertThat(actual.getUserId()).isEqualTo(command.getUserId());
        assertThat(actual.getParentReply()).isEqualTo(parentReply);
        assertThat(actual.getMessage()).isEqualTo(command.getMessage());
        assertThat(actual.hasParentReply()).isTrue();
        verify(replyDomainService).validateParentReplyBelongsToPost(command.getPostId(), parent);
        verify(replyRepository).increaseReplyCount(DomainTestFixtures.replyId());
    }

    @Test
    @DisplayName("ReplyCommandService.createReply は投稿が存在しない場合に PostNotFoundException を送出する")
    void should_throwPostNotFoundException_when_postDoesNotExist() {
        final CreateReplyCommand command = CreateReplyCommand.of(
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                ParentReply.root(),
                DomainTestFixtures.replyMessage()
        );
        when(postDomainService.isExistPost(command.getPostId())).thenReturn(false);

        assertThatThrownBy(() -> newService().createReply(command))
                .isInstanceOf(PostNotFoundException.class);

        verify(replyRepository, never()).saveReply(any());
    }

    @Test
    @DisplayName("ReplyCommandService.createReply はユーザーが存在しない場合に UserNotFoundException を送出する")
    void should_throwUserNotFoundException_when_userDoesNotExist() {
        final CreateReplyCommand command = CreateReplyCommand.of(
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                ParentReply.root(),
                DomainTestFixtures.replyMessage()
        );
        when(postDomainService.isExistPost(command.getPostId())).thenReturn(true);
        when(userDomainService.isExistUser(command.getUserId())).thenReturn(false);

        assertThatThrownBy(() -> newService().createReply(command))
                .isInstanceOf(UserNotFoundException.class);

        verify(replyRepository, never()).saveReply(any());
    }

    @Test
    @DisplayName("ReplyCommandService.createReply は親返信が存在しない場合に ReplyNotFoundException を送出する")
    void should_throwReplyNotFoundException_when_parentReplyDoesNotExist() {
        final CreateReplyCommand command = CreateReplyCommand.of(
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                ParentReply.of(DomainTestFixtures.replyId()),
                DomainTestFixtures.replyMessage()
        );
        when(postDomainService.isExistPost(command.getPostId())).thenReturn(true);
        when(userDomainService.isExistUser(command.getUserId())).thenReturn(true);
        when(replyDomainService.findReplyById(DomainTestFixtures.replyId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> newService().createReply(command))
                .isInstanceOf(ReplyNotFoundException.class);

        verify(replyRepository, never()).saveReply(any());
    }

    @Test
    @DisplayName("ReplyCommandService.createReply は保存失敗を ReplyCommandFailedException に変換する")
    void should_throwUsecaseException_when_saveFails() {
        final CreateReplyCommand command = CreateReplyCommand.of(
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                ParentReply.root(),
                DomainTestFixtures.replyMessage()
        );
        when(postDomainService.isExistPost(command.getPostId())).thenReturn(true);
        when(userDomainService.isExistUser(command.getUserId())).thenReturn(true);
        doThrow(new DataAccessResourceFailureException("db down")).when(replyRepository).saveReply(any());

        assertThatThrownBy(() -> newService().createReply(command))
                .isInstanceOf(ReplyCommandFailedException.class)
                .satisfies(throwable -> {
                    final ReplyCommandFailedException exception = (ReplyCommandFailedException) throwable;
                    assertThat(exception.getDetails())
                            .containsEntry("target", "reply")
                            .containsEntry("reason", "save_failed");
                });
    }

    @Test
    @DisplayName("ReplyCommandService.createReply は返信数更新失敗を ReplyCommandFailedException に変換する")
    void should_throwUsecaseException_when_increaseReplyCountFails() {
        final ParentReply parentReply = ParentReply.of(DomainTestFixtures.replyId());
        final CreateReplyCommand command = CreateReplyCommand.of(
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                parentReply,
                DomainTestFixtures.replyMessage()
        );
        final Reply parent = DomainTestFixtures.reply();
        when(postDomainService.isExistPost(command.getPostId())).thenReturn(true);
        when(userDomainService.isExistUser(command.getUserId())).thenReturn(true);
        when(replyDomainService.findReplyById(DomainTestFixtures.replyId())).thenReturn(Optional.of(parent));
        doThrow(new DataAccessResourceFailureException("db down"))
                .when(replyRepository)
                .increaseReplyCount(DomainTestFixtures.replyId());

        assertThatThrownBy(() -> newService().createReply(command))
                .isInstanceOf(ReplyCommandFailedException.class)
                .satisfies(throwable -> {
                    final ReplyCommandFailedException exception = (ReplyCommandFailedException) throwable;
                    assertThat(exception.getDetails())
                            .containsEntry("target", "reply")
                            .containsEntry("reason", "increase_reply_count_failed")
                            .containsEntry("rejectedValue", DomainTestFixtures.replyId().getValue());
                });
    }

    private static ReplyIdGenerator fixedReplyIdGenerator(final String value) {
        return () -> jp.i432kg.footprint.domain.value.ReplyId.of(value);
    }
}
