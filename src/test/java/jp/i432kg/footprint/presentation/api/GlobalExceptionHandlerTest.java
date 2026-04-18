package jp.i432kg.footprint.presentation.api;

import jakarta.validation.Valid;
import jp.i432kg.footprint.application.exception.resource.PostNotFoundException;
import jp.i432kg.footprint.application.exception.usecase.PostCommandFailedException;
import jp.i432kg.footprint.domain.exception.InvalidValueException;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.logging.masking.SensitiveDataMasker;
import jp.i432kg.footprint.presentation.api.request.SignUpRequest;
import jp.i432kg.footprint.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private SensitiveDataMasker sensitiveDataMasker;

    @Test
    @DisplayName("GlobalExceptionHandler は PostNotFoundException を 404 の ProblemDetail へ変換する")
    void should_createNotFoundProblemDetail_when_postNotFoundExceptionIsHandled() {
        final PostNotFoundException exception = new PostNotFoundException(PostId.of("01ARZ3NDEKTSV4RRFFQ69G5FAX"));
        when(sensitiveDataMasker.maskMap(exception.getDetails())).thenReturn(exception.getDetails());

        final ProblemDetail actual = new GlobalExceptionHandler(sensitiveDataMasker).handlePostNotFound(exception);

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(actual.getTitle()).isEqualTo("Post Not Found");
        assertThat(actual.getDetail()).isEqualTo(exception.getMessage());
        assertThat(actual.getProperties())
                .containsEntry("errorCode", ErrorCode.POST_NOT_FOUND)
                .containsEntry("details", exception.getDetails());
    }

    @Test
    @DisplayName("GlobalExceptionHandler は InvalidValueException を 400 の ProblemDetail へ変換する")
    void should_createBadRequestProblemDetail_when_invalidValueExceptionIsHandled() {
        final InvalidValueException exception = InvalidValueException.required("email");
        final Map<String, Object> maskedDetails = new java.util.LinkedHashMap<>();
        maskedDetails.put("target", "email");
        maskedDetails.put("reason", "required");
        maskedDetails.put("rejectedValue", null);
        when(sensitiveDataMasker.maskMap(exception.getDetails())).thenReturn(maskedDetails);

        final ProblemDetail actual = new GlobalExceptionHandler(sensitiveDataMasker).handleInvalidValue(exception);

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(actual.getTitle()).isEqualTo("Invalid Value");
        assertThat(actual.getDetail()).isEqualTo(exception.getMessage());
        assertThat(actual.getProperties())
                .containsEntry("errorCode", ErrorCode.DOMAIN_INVALID_VALUE)
                .containsEntry("details", maskedDetails);
    }

    @Test
    @DisplayName("GlobalExceptionHandler は MethodArgumentNotValidException を validation error の ProblemDetail へ変換する")
    void should_createValidationProblemDetail_when_methodArgumentNotValidExceptionIsHandled() throws Exception {
        final SignUpRequest target = new SignUpRequest();
        final BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "signUpRequest");
        bindingResult.addError(new FieldError("signUpRequest", "userName", "ab", false, null, null, "size"));
        when(sensitiveDataMasker.maskRejectedValue("userName", "ab")).thenReturn("ab");

        final ProblemDetail actual = new GlobalExceptionHandler(sensitiveDataMasker)
                .handleMethodArgumentNotValid(new MethodArgumentNotValidException(methodParameter(), bindingResult));

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(actual.getTitle()).isEqualTo("Validation Error");
        assertThat(actual.getDetail()).isEqualTo("リクエストの形式が不正です。");
        assertThat(actual.getProperties()).containsEntry("errorCode", "DOMAIN_INVALID_VALUE");
        final Map<String, Object> details = castMap(actual.getProperties().get("details"));
        final List<Map<String, Object>> errors = castList(details.get("errors"));
        assertThat(errors).singleElement().satisfies(error -> {
            assertThat(error).containsEntry("field", "userName");
            assertThat(error).containsEntry("message", "size");
            assertThat(error).containsEntry("rejectedValue", "ab");
        });
    }

    @Test
    @DisplayName("GlobalExceptionHandler は HttpMessageNotReadableException を validation error の ProblemDetail へ変換する")
    void should_createValidationProblemDetail_when_httpMessageNotReadableExceptionIsHandled() {
        final HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException("bad json", new IOException("broken body"), null);

        final ProblemDetail actual = new GlobalExceptionHandler(sensitiveDataMasker)
                .handleHttpMessageNotReadable(exception);

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(actual.getTitle()).isEqualTo("Validation Error");
        assertThat(actual.getDetail()).isEqualTo("リクエストボディを解析できませんでした。");
        final Map<String, Object> details = castMap(actual.getProperties().get("details"));
        final List<Map<String, Object>> errors = castList(details.get("errors"));
        assertThat(errors).singleElement().satisfies(error -> {
            assertThat(error).containsEntry("field", "requestBody");
            assertThat(error).containsEntry("message", "not readable");
            assertThat(error).containsEntry("rejectedValue", null);
        });
    }

    @Test
    @DisplayName("GlobalExceptionHandler は UseCaseExecutionException を対応ステータスの ProblemDetail へ変換する")
    void should_createUseCaseProblemDetail_when_useCaseExecutionExceptionIsHandled() {
        final PostCommandFailedException exception =
                PostCommandFailedException.persistenceFailed("post-01", new IOException("db down"));
        when(sensitiveDataMasker.maskMap(exception.getDetails())).thenReturn(exception.getDetails());

        final ProblemDetail actual = new GlobalExceptionHandler(sensitiveDataMasker)
                .handleUseCaseExecutionException(exception);

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(actual.getTitle()).isEqualTo("Use Case Error");
        assertThat(actual.getProperties()).containsEntry("errorCode", ErrorCode.POST_COMMAND_FAILED);
    }

    @Test
    @DisplayName("GlobalExceptionHandler は想定外例外を 500 の ProblemDetail へ変換する")
    void should_createInternalServerErrorProblemDetail_when_unexpectedExceptionIsHandled() {
        final ProblemDetail actual = new GlobalExceptionHandler(sensitiveDataMasker)
                .handleException(new RuntimeException("unexpected"));

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(actual.getTitle()).isEqualTo("Internal Server Error");
        assertThat(actual.getDetail()).isEqualTo("サーバー内部でエラーが発生しました。");
        assertThat(actual.getProperties()).containsEntry("errorCode", "UNEXPECTED_ERROR");
    }

    @Test
    @DisplayName("GlobalExceptionHandler は独自例外の details に SensitiveDataMasker の結果を使う")
    void should_useMaskedDetails_when_baseExceptionIsHandled() {
        final InvalidValueException exception = InvalidValueException.invalid("password", "Secret12", "invalid_format");
        final Map<String, Object> maskedDetails = Map.of(
                "target", "password",
                "reason", "invalid_format",
                "rejectedValue", "****"
        );
        when(sensitiveDataMasker.maskMap(exception.getDetails())).thenReturn(maskedDetails);

        final ProblemDetail actual = new GlobalExceptionHandler(sensitiveDataMasker).handleInvalidValue(exception);

        assertThat(actual.getProperties()).containsEntry("details", maskedDetails);
        verify(sensitiveDataMasker, times(2)).maskMap(exception.getDetails());
    }

    private static MethodParameter methodParameter() throws NoSuchMethodException {
        final Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod("dummy", SignUpRequest.class);
        return new MethodParameter(method, 0);
    }

    @SuppressWarnings("unused")
    private void dummy(@Valid final SignUpRequest request) {
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> castMap(final Object value) {
        return (Map<String, Object>) value;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> castList(final Object value) {
        return (List<Map<String, Object>>) value;
    }

}
