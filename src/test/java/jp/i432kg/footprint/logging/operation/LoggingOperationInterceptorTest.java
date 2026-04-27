package jp.i432kg.footprint.logging.operation;

import jp.i432kg.footprint.logging.LoggingOperations;
import jp.i432kg.footprint.logging.access.AccessLogFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.method.HandlerMethod;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingOperationInterceptorTest {

    private final LoggingOperationInterceptor interceptor = new LoggingOperationInterceptor();
    private final TestController controller = new TestController();

    @Test
    @DisplayName("LoggingOperationInterceptor は annotation の operation を request 文脈へ設定する")
    void should_setOperationIntoRequestContext_when_annotationExists() throws NoSuchMethodException {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final HandlerMethod handlerMethod =
                new HandlerMethod(controller, TestController.class.getDeclaredMethod("annotated"));

        final boolean actual = interceptor.preHandle(request, new MockHttpServletResponse(), handlerMethod);

        assertThat(actual).isTrue();
        assertThat(AccessLogFilter.findContext(request))
                .flatMap(context -> context.operation())
                .contains(LoggingOperations.POST_TIMELINE_FETCH);
    }

    @Test
    @DisplayName("LoggingOperationInterceptor は annotation がない handler では request 文脈を変更しない")
    void should_notSetOperation_when_annotationDoesNotExist() throws NoSuchMethodException {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final HandlerMethod handlerMethod =
                new HandlerMethod(controller, TestController.class.getDeclaredMethod("plain"));

        final boolean actual = interceptor.preHandle(request, new MockHttpServletResponse(), handlerMethod);

        assertThat(actual).isTrue();
        assertThat(AccessLogFilter.findContext(request)).isEmpty();
    }

    static class TestController {

        @GetMapping("/annotated")
        @LogOperation(LoggingOperations.POST_TIMELINE_FETCH)
        void annotated() {
        }

        @GetMapping("/plain")
        void plain() {
        }
    }
}
