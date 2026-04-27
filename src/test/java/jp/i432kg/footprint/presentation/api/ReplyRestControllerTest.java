package jp.i432kg.footprint.presentation.api;

import jakarta.servlet.http.HttpServletRequest;
import jp.i432kg.footprint.application.command.model.CreateReplyCommand;
import jp.i432kg.footprint.application.command.service.ReplyCommandService;
import jp.i432kg.footprint.application.query.model.ReplySummary;
import jp.i432kg.footprint.application.query.service.ReplyQueryService;
import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.infrastructure.security.UserDetailsImpl;
import jp.i432kg.footprint.infrastructure.security.mapper.AuthMapper;
import jp.i432kg.footprint.logging.LoggingEvents;
import jp.i432kg.footprint.logging.access.AccessLogFilter;
import jp.i432kg.footprint.presentation.api.request.ReplyRequest;
import jp.i432kg.footprint.presentation.api.response.ReplyItemResponse;
import jp.i432kg.footprint.presentation.api.response.mapper.ReplyResponseMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReplyRestControllerTest {

    @Mock
    private ReplyCommandService replyCommandService;

    @Mock
    private ReplyQueryService replyQueryService;

    @Mock
    private ReplyResponseMapper replyResponseMapper;

    @Test
    @DisplayName("ReplyRestController は親返信配下のネスト返信一覧を 200 で返す")
    void should_returnNestedReplies_when_getNextRepliesIsCalled() {
        final List<ReplySummary> summaries = List.of(new ReplySummary());
        final List<ReplyItemResponse> responses = List.of(
                ReplyItemResponse.of("reply-01", "post-01", null, "reply", 0, java.time.OffsetDateTime.now())
        );
        final MockHttpServletRequest request = new MockHttpServletRequest();
        when(replyQueryService.listNestedReplies(DomainTestFixtures.replyId())).thenReturn(summaries);
        when(replyResponseMapper.fromList(summaries)).thenReturn(responses);

        final var actual = newController().getNextReplies(DomainTestFixtures.REPLY_ID, request);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isEqualTo(responses);
        assertThat(AccessLogFilter.findContext(request))
                .map(jp.i432kg.footprint.logging.access.AccessLogContext::event)
                .contains(LoggingEvents.REPLY_LIST_FETCH);
        assertThat(logFields(request))
                .containsEntry("parentReplyId", DomainTestFixtures.REPLY_ID)
                .containsEntry("items", 1);
    }

    @Test
    @DisplayName("ReplyRestController は parentReplyId 未指定時にルート返信として 201 を返す")
    void should_createRootReply_when_parentReplyIdIsNull() {
        final ReplyRequest request = new ReplyRequest();
        request.setParentReplyId(null);
        request.setMessage("reply");

        final var actual = newController().reply(DomainTestFixtures.POST_ID, request, userDetails());

        final ArgumentCaptor<CreateReplyCommand> captor = ArgumentCaptor.forClass(CreateReplyCommand.class);
        verify(replyCommandService).createReply(captor.capture());
        final CreateReplyCommand command = captor.getValue();

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(command.getPostId()).isEqualTo(DomainTestFixtures.postId());
        assertThat(command.getUserId()).isEqualTo(DomainTestFixtures.userId());
        assertThat(command.getParentReply().getReplyId()).isNull();
        assertThat(command.getMessage().getValue()).isEqualTo("reply");
    }

    @Test
    @DisplayName("ReplyRestController は parentReplyId 指定時に子返信として 201 を返す")
    void should_createChildReply_when_parentReplyIdIsPresent() {
        final ReplyRequest request = new ReplyRequest();
        request.setParentReplyId(DomainTestFixtures.REPLY_ID);
        request.setMessage("reply");

        final var actual = newController().reply(DomainTestFixtures.POST_ID, request, userDetails());

        final ArgumentCaptor<CreateReplyCommand> captor = ArgumentCaptor.forClass(CreateReplyCommand.class);
        verify(replyCommandService).createReply(captor.capture());
        final CreateReplyCommand command = captor.getValue();

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(command.getParentReply().getReplyId()).isEqualTo(DomainTestFixtures.replyId());
    }

    private ReplyRestController newController() {
        return new ReplyRestController(replyCommandService, replyQueryService, replyResponseMapper);
    }

    private static Map<String, Object> logFields(final HttpServletRequest request) {
        return AccessLogFilter.findContext(request).orElseThrow().fields();
    }

    private static UserDetailsImpl userDetails() {
        return UserDetailsImpl.fromEntity(
                new AuthMapper.AuthUserEntity(
                        DomainTestFixtures.userId(),
                        "user@example.com",
                        "user",
                        "password"
                )
        );
    }

}
