package jp.i432kg.footprint.logging.trace;

import jakarta.servlet.ServletException;
import jp.i432kg.footprint.application.exception.resource.PostNotFoundException;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.logging.masking.SensitiveDataMasker;
import jp.i432kg.footprint.logging.operation.FailureEventResolver;
import jp.i432kg.footprint.presentation.api.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TraceIdFilterTest {

    private final TraceIdFilter traceIdFilter = new TraceIdFilter();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler(new FailureEventResolver(), new SensitiveDataMasker()))
                .addFilters(traceIdFilter)
                .build();
    }

    @Test
    @DisplayName("TraceIdFilter は traceId を MDC と request/response へ設定し、処理後に MDC から除去する")
    void should_setTraceIdAndClearMdc_when_requestIsProcessed() throws ServletException, IOException {
        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        traceIdFilter.doFilter(request, response, (req, res) -> {
            final String traceId = MDC.get(TraceIdFilter.MDC_KEY);

            assertThat(traceId).matches("^[0-9A-HJKMNP-TV-Z]{26}$");
            assertThat(req.getAttribute(TraceIdFilter.REQUEST_ATTRIBUTE)).isEqualTo(traceId);
            assertThat(((MockHttpServletResponse) res).getHeader(TraceIdFilter.HEADER_NAME)).isEqualTo(traceId);
        });

        assertThat(MDC.get(TraceIdFilter.MDC_KEY)).isNull();
    }

    @Test
    @DisplayName("TraceIdFilter は下流で例外が発生しても MDC を確実に除去する")
    void should_clearMdcEvenWhen_downstreamThrows() {
        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        assertThatThrownBy(() -> traceIdFilter.doFilter(request, response, (req, res) -> {
            assertThat(MDC.get(TraceIdFilter.MDC_KEY)).isNotBlank();
            throw new IllegalStateException("boom");
        }))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("boom");

        assertThat(response.getHeader(TraceIdFilter.HEADER_NAME)).isNotBlank();
        assertThat(MDC.get(TraceIdFilter.MDC_KEY)).isNull();
    }

    @Test
    @DisplayName("TraceIdFilter は正常レスポンスへ X-Trace-Id を付与する")
    void should_addTraceIdHeader_when_requestSucceeds() throws Exception {
        mockMvc.perform(get("/trace/success"))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceIdFilter.HEADER_NAME));
    }

    @Test
    @DisplayName("TraceIdFilter は GlobalExceptionHandler を通るエラーレスポンスにも X-Trace-Id を付与する")
    void should_addTraceIdHeader_when_exceptionIsHandled() throws Exception {
        mockMvc.perform(get("/trace/posts/01ARZ3NDEKTSV4RRFFQ69G5FAX"))
                .andExpect(status().isNotFound())
                .andExpect(header().exists(TraceIdFilter.HEADER_NAME));
    }

    @RestController
    static class TestController {

        @GetMapping("/trace/success")
        String success() {
            return "ok";
        }

        @GetMapping("/trace/posts/{postId}")
        String notFound() {
            throw new PostNotFoundException(PostId.of("01ARZ3NDEKTSV4RRFFQ69G5FAX"));
        }
    }
}
