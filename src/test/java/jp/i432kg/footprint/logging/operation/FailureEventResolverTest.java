package jp.i432kg.footprint.logging.operation;

import jakarta.validation.ConstraintViolationException;
import jp.i432kg.footprint.logging.LoggingEvents;
import jp.i432kg.footprint.logging.LoggingOperations;
import jp.i432kg.footprint.logging.access.AccessLogFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FailureEventResolverTest {

    private final FailureEventResolver resolver = new FailureEventResolver();

    @Test
    @DisplayName("FailureEventResolver は POST_CREATE の imageFile エラーを upload rejected へ解決する")
    void should_resolveUploadRejected_when_postCreateAndImageFileError() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        AccessLogFilter.setOperation(request, LoggingOperations.POST_CREATE);

        final String actual = resolver.resolveValidationEvent(
                request,
                new BindException(new BeanPropertyBindingResult(new Object(), "postRequest")),
                List.of(Map.of("target", "imageFile"))
        );

        assertThat(actual).isEqualTo(LoggingEvents.POST_CREATE_UPLOAD_REJECTED);
    }

    @Test
    @DisplayName("FailureEventResolver は POST_CREATE の multipart 欠落を upload rejected へ解決する")
    void should_resolveUploadRejected_when_postCreateAndMissingMultipart() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        AccessLogFilter.setOperation(request, LoggingOperations.POST_CREATE);

        final String actual = resolver.resolveValidationEvent(
                request,
                new MissingServletRequestPartException("imageFile"),
                List.of(Map.of("target", "imageFile"))
        );

        assertThat(actual).isEqualTo(LoggingEvents.POST_CREATE_UPLOAD_REJECTED);
    }

    @Test
    @DisplayName("FailureEventResolver は POST_CREATE の通常バリデーション失敗を validation fail へ解決する")
    void should_resolvePostCreateValidationFail_when_postCreateAndNonFileError() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        AccessLogFilter.setOperation(request, LoggingOperations.POST_CREATE);

        final String actual = resolver.resolveValidationEvent(
                request,
                new BindException(new BeanPropertyBindingResult(new Object(), "postRequest")),
                List.of(Map.of("target", "comment"))
        );

        assertThat(actual).isEqualTo(LoggingEvents.POST_CREATE_VALIDATION_FAIL);
    }

    @Test
    @DisplayName("FailureEventResolver は ME_POSTS_FETCH の paging validation を POST_LAST_ID_INVALID へ解決する")
    void should_resolvePostLastIdInvalid_when_myPostsPagingValidationFails() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        AccessLogFilter.setOperation(request, LoggingOperations.ME_POSTS_FETCH);

        final String actual = resolver.resolveValidationEvent(
                request,
                new ConstraintViolationException(Set.of()),
                List.of(Map.of("target", "lastId"))
        );

        assertThat(actual).isEqualTo(LoggingEvents.POST_LAST_ID_INVALID);
    }

    @Test
    @DisplayName("FailureEventResolver は operation 未設定時に request 共通 validation event を返す")
    void should_resolveGenericValidationFail_when_operationIsMissing() {
        final String actual = resolver.resolveValidationEvent(
                new MockHttpServletRequest(),
                new BindException(new BeanPropertyBindingResult(new Object(), "request")),
                List.of(Map.of("target", "size"))
        );

        assertThat(actual).isEqualTo(LoggingEvents.REQUEST_VALIDATION_FAIL);
    }
}
