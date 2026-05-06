package jp.i432kg.footprint.logging.access;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import jp.i432kg.footprint.application.exception.resource.PostNotFoundException;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.infrastructure.security.UserDetailsImpl;
import jp.i432kg.footprint.infrastructure.security.mapper.AuthMapper;
import jp.i432kg.footprint.logging.LoggingCategories;
import jp.i432kg.footprint.logging.LoggingEvents;
import jp.i432kg.footprint.logging.masking.SensitiveDataMasker;
import jp.i432kg.footprint.logging.operation.FailureEventResolver;
import jp.i432kg.footprint.logging.trace.TraceIdFilter;
import jp.i432kg.footprint.presentation.api.GlobalExceptionHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.event.KeyValuePair;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccessLogFilterTest {

    private final AccessLogFilter accessLogFilter = new AccessLogFilter();
    private final TraceIdFilter traceIdFilter = new TraceIdFilter();

    private ListAppender<ILoggingEvent> listAppender;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        final Logger logger = (Logger) LoggerFactory.getLogger(LoggingCategories.ACCESS);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler(new FailureEventResolver(), new SensitiveDataMasker()))
                .addFilters(traceIdFilter, accessLogFilter)
                .build();
    }

    @AfterEach
    void tearDown() {
        final Logger logger = (Logger) LoggerFactory.getLogger(LoggingCategories.ACCESS);
        logger.detachAppender(listAppender);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("AccessLogFilter は method/path/status/duration を access カテゴリへ出力する")
    void should_logMethodPathStatusAndDuration_when_requestSucceeds() throws ServletException, IOException {
        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/posts");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        accessLogFilter.doFilter(request, response, (req, res) -> ((MockHttpServletResponse) res).setStatus(204));

        final ILoggingEvent event = singleEvent();
        final Map<String, Object> keyValues = keyValues(event);

        assertThat(event.getLoggerName()).isEqualTo(LoggingCategories.ACCESS);
        assertThat(event.getLevel()).isEqualTo(Level.INFO);
        assertThat(event.getFormattedMessage()).isEqualTo("HTTP access completed");
        assertThat(keyValues)
                .containsEntry("event", LoggingEvents.HTTP_ACCESS)
                .containsEntry("method", "GET")
                .containsEntry("path", "/api/posts")
                .containsEntry("status", 204)
                .containsKey("durationMs");
    }

    @Test
    @DisplayName("AccessLogFilter は認証済みユーザーがいる場合 userId と username を出力する")
    void should_logUserFields_when_authenticatedUserExists() throws ServletException, IOException {
        final UserDetailsImpl principal = UserDetailsImpl.fromEntity(new AuthMapper.AuthUserEntity(
                jp.i432kg.footprint.domain.value.UserId.of("01JQW8D4Q3G9Y2X6N7M8P9R0ST"),
                "user@example.com",
                "user",
                "hashed-password"
        ));
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(principal, null, principal.getAuthorities())
        );

        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        accessLogFilter.doFilter(request, response, (req, res) -> ((MockHttpServletResponse) res).setStatus(200));

        final ILoggingEvent event = singleEvent();
        final Map<String, Object> keyValues = keyValues(event);

        assertThat(keyValues)
                .containsEntry("userId", "01JQW8D4Q3G9Y2X6N7M8P9R0ST")
                .containsEntry("username", "user");
    }

    @Test
    @DisplayName("AccessLogFilter は controller が設定した event と追加項目を access ログへ含める")
    void should_logControllerSuppliedEventAndFields_when_requestAttributesExist() throws ServletException, IOException {
        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/posts/search");
        final MockHttpServletResponse response = new MockHttpServletResponse();
        AccessLogFilter.setEvent(request, LoggingEvents.POST_SEARCH_FETCH);
        AccessLogFilter.addField(request, "lastIdPresent", true);
        AccessLogFilter.addField(request, "size", 10);
        AccessLogFilter.addField(request, "items", 3);

        accessLogFilter.doFilter(request, response, (req, res) -> ((MockHttpServletResponse) res).setStatus(200));

        final ILoggingEvent event = singleEvent();
        final Map<String, Object> keyValues = keyValues(event);

        assertThat(keyValues)
                .containsEntry("event", LoggingEvents.POST_SEARCH_FETCH)
                .containsEntry("lastIdPresent", true)
                .containsEntry("size", 10)
                .containsEntry("items", 3);
        assertThat(AccessLogFilter.findContext(request))
                .map(AccessLogContext::event)
                .contains(LoggingEvents.POST_SEARCH_FETCH);
    }

    @Test
    @DisplayName("AccessLogFilter は TraceIdFilter と併用したとき access ログへ traceId を引き継ぐ")
    void should_includeTraceIdInMdc_when_usedWithTraceIdFilter() throws Exception {
        mockMvc.perform(get("/trace/posts/01ARZ3NDEKTSV4RRFFQ69G5FAX"))
                .andExpect(status().isNotFound());

        final ILoggingEvent event = singleEvent();

        assertThat(event.getMDCPropertyMap())
                .containsKey(TraceIdFilter.MDC_KEY);
        assertThat(event.getMDCPropertyMap().get(TraceIdFilter.MDC_KEY))
                .matches("^[0-9A-HJKMNP-TV-Z]{26}$");
        assertThat(keyValues(event)).containsEntry("status", 404);
    }

    @Test
    @DisplayName("AccessLogFilter はハンドリングされない例外でも 500 として access ログを残す")
    void should_logInternalServerError_when_downstreamThrowsUnhandledException() {
        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/posts");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        assertThatThrownBy(() -> accessLogFilter.doFilter(request, response, (req, res) -> {
            throw new IllegalStateException("boom");
        }))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("boom");

        final ILoggingEvent event = singleEvent();

        assertThat(keyValues(event)).containsEntry("status", 500);
    }

    private ILoggingEvent singleEvent() {
        assertThat(listAppender.list).hasSize(1);
        return listAppender.list.getFirst();
    }

    private Map<String, Object> keyValues(final ILoggingEvent event) {
        return event.getKeyValuePairs().stream()
                .collect(Collectors.toMap(
                        pair -> pair.key,
                        pair -> pair.value,
                        (left, right) -> right
                ));
    }

    @RestController
    static class TestController {

        @GetMapping("/trace/success")
        String success(final HttpServletResponse response) {
            return "ok";
        }

        @GetMapping("/trace/posts/{postId}")
        String notFound() {
            throw new PostNotFoundException(PostId.of("01ARZ3NDEKTSV4RRFFQ69G5FAX"));
        }
    }
}
