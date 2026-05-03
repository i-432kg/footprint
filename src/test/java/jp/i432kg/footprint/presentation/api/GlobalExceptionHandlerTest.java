package jp.i432kg.footprint.presentation.api;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.Valid;
import jp.i432kg.footprint.application.exception.ApplicationException;
import jp.i432kg.footprint.application.exception.resource.PostNotFoundException;
import jp.i432kg.footprint.application.exception.resource.ReplyNotFoundException;
import jp.i432kg.footprint.application.exception.resource.UserNotFoundException;
import jp.i432kg.footprint.application.exception.usecase.PostCommandFailedException;
import jp.i432kg.footprint.domain.exception.EmailAlreadyUsedException;
import jp.i432kg.footprint.domain.exception.InvalidModelException;
import jp.i432kg.footprint.domain.exception.InvalidValueException;
import jp.i432kg.footprint.domain.exception.ReplyPostMismatchException;
import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.exception.ErrorCode;
import jp.i432kg.footprint.logging.operation.FailureEventResolver;
import jp.i432kg.footprint.logging.masking.SensitiveDataMasker;
import jp.i432kg.footprint.presentation.api.request.SignUpRequest;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private SensitiveDataMasker sensitiveDataMasker;

    private final FailureEventResolver failureEventResolver = new FailureEventResolver();

    @Test
    @DisplayName("GlobalExceptionHandler は PostNotFoundException を 404 の ProblemDetail へ変換する")
    void should_createNotFoundProblemDetail_when_postNotFoundExceptionIsHandled() {
        final PostNotFoundException exception = new PostNotFoundException(PostId.of("01ARZ3NDEKTSV4RRFFQ69G5FAX"));
        when(sensitiveDataMasker.maskMap(exception.getDetails())).thenReturn(exception.getDetails());

        final ProblemDetail actual = newHandler().handlePostNotFound(exception);

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(actual.getTitle()).isEqualTo("Post Not Found");
        assertThat(actual.getProperties()).containsEntry("errorCode", ErrorCode.POST_NOT_FOUND);
        assertThat(castMap(property(actual)))
                .containsEntry("target", "post")
                .containsEntry("reason", "not_found")
                .containsEntry("resourceId", "01ARZ3NDEKTSV4RRFFQ69G5FAX");
    }

    @Test
    @DisplayName("GlobalExceptionHandler は ReplyNotFoundException を 404 の ProblemDetail へ変換する")
    void should_createNotFoundProblemDetail_when_replyNotFoundExceptionIsHandled() {
        final ReplyNotFoundException exception = new ReplyNotFoundException(ReplyId.of("01ARZ3NDEKTSV4RRFFQ69G5FAZ"));
        when(sensitiveDataMasker.maskMap(exception.getDetails())).thenReturn(exception.getDetails());

        final ProblemDetail actual = newHandler().handleReplyNotFound(exception);

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(actual.getTitle()).isEqualTo("Reply Not Found");
        assertThat(actual.getProperties()).containsEntry("errorCode", ErrorCode.REPLY_NOT_FOUND);
        assertThat(castMap(property(actual)))
                .containsEntry("target", "reply")
                .containsEntry("reason", "not_found")
                .containsEntry("resourceId", "01ARZ3NDEKTSV4RRFFQ69G5FAZ");
    }

    @Test
    @DisplayName("GlobalExceptionHandler は UserNotFoundException を 404 の ProblemDetail へ変換する")
    void should_createNotFoundProblemDetail_when_userNotFoundExceptionIsHandled() {
        final UserNotFoundException exception = new UserNotFoundException(UserId.of("01ARZ3NDEKTSV4RRFFQ69G5FAV"));
        when(sensitiveDataMasker.maskMap(exception.getDetails())).thenReturn(exception.getDetails());

        final ProblemDetail actual = newHandler().handleUserNotFound(exception);

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(actual.getTitle()).isEqualTo("User Not Found");
        assertThat(actual.getProperties()).containsEntry("errorCode", ErrorCode.USER_NOT_FOUND);
        assertThat(castMap(property(actual)))
                .containsEntry("target", "user")
                .containsEntry("reason", "not_found")
                .containsEntry("resourceId", "01ARZ3NDEKTSV4RRFFQ69G5FAV");
    }

    @Test
    @DisplayName("GlobalExceptionHandler は InvalidValueException を 400 の ProblemDetail へ変換する")
    void should_createBadRequestProblemDetail_when_invalidValueExceptionIsHandled() {
        final InvalidValueException exception = InvalidValueException.required("email");
        final Map<String, Object> maskedDetails = new LinkedHashMap<>();
        maskedDetails.put("target", "email");
        maskedDetails.put("reason", "required");
        maskedDetails.put("rejectedValue", null);
        when(sensitiveDataMasker.maskMap(exception.getDetails())).thenReturn(maskedDetails);

        final ProblemDetail actual = newHandler().handleInvalidValue(exception);

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(actual.getTitle()).isEqualTo("Invalid Value");
        assertThat(actual.getProperties())
                .containsEntry("errorCode", ErrorCode.DOMAIN_INVALID_VALUE)
                .containsEntry("details", maskedDetails);
    }

    @Test
    @DisplayName("GlobalExceptionHandler は EmailAlreadyUsedException を 409 の ProblemDetail へ変換する")
    void should_createConflictProblemDetail_when_emailAlreadyUsedExceptionIsHandled() {
        final EmailAlreadyUsedException exception = new EmailAlreadyUsedException(EmailAddress.of("user@example.com"));
        final Map<String, Object> maskedDetails = Map.of(
                "target", "email",
                "reason", "already_used",
                "rejectedValue", "u***@example.com"
        );
        when(sensitiveDataMasker.maskMap(exception.getDetails())).thenReturn(maskedDetails);

        final ProblemDetail actual = newHandler().handleEmailAlreadyUsed(exception);

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(actual.getTitle()).isEqualTo("Email Already Used");
        assertThat(actual.getProperties())
                .containsEntry("errorCode", ErrorCode.EMAIL_ALREADY_USED)
                .containsEntry("details", maskedDetails);
    }

    @Test
    @DisplayName("GlobalExceptionHandler は ReplyPostMismatchException を 400 の ProblemDetail へ変換する")
    void should_createBadRequestProblemDetail_when_replyPostMismatchExceptionIsHandled() {
        final ReplyPostMismatchException exception =
                new ReplyPostMismatchException(PostId.of("01ARZ3NDEKTSV4RRFFQ69G5FAX"), PostId.of("01ARZ3NDEKTSV4RRFFQ69G5FAY"));
        when(sensitiveDataMasker.maskMap(exception.getDetails())).thenReturn(exception.getDetails());

        final ProblemDetail actual = newHandler().handleReplyPostMismatch(exception);

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(actual.getTitle()).isEqualTo("Reply Post Mismatch");
        assertThat(actual.getProperties()).containsEntry("errorCode", ErrorCode.REPLY_POST_MISMATCH);
        assertThat(castMap(property(actual)))
                .containsEntry("target", "reply.postId")
                .containsEntry("reason", "post_mismatch")
                .containsEntry("expectedPostId", "01ARZ3NDEKTSV4RRFFQ69G5FAX")
                .containsEntry("actualPostId", "01ARZ3NDEKTSV4RRFFQ69G5FAY")
                .doesNotContainKey("rejectedValue");
    }

    @Test
    @DisplayName("GlobalExceptionHandler は MethodArgumentNotValidException を validation error の ProblemDetail へ変換する")
    void should_createValidationProblemDetail_when_methodArgumentNotValidExceptionIsHandled() throws Exception {
        final SignUpRequest target = new SignUpRequest();
        final BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "signUpRequest");
        bindingResult.addError(new FieldError("signUpRequest", "userName", "ab", false, null, null, "size"));
        when(sensitiveDataMasker.maskRejectedValue("userName", "ab")).thenReturn("ab");

        final ProblemDetail actual = newHandler()
                .handleMethodArgumentNotValid(
                        new MethodArgumentNotValidException(methodParameter(), bindingResult),
                        newRequest()
                );

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(actual.getTitle()).isEqualTo("Validation Error");
        final List<Map<String, Object>> errors = errors(actual);
        assertThat(errors).singleElement().satisfies(error -> {
            assertThat(error).containsEntry("target", "userName");
            assertThat(error).containsEntry("reason", "size");
            assertThat(error).containsEntry("rejectedValue", "ab");
        });
    }

    @Test
    @DisplayName("GlobalExceptionHandler は BindException を validation error の ProblemDetail へ変換する")
    void should_createValidationProblemDetail_when_bindExceptionIsHandled() {
        final SignUpRequest target = new SignUpRequest();
        final BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "signUpRequest");
        bindingResult.addError(new FieldError("signUpRequest", "email", "invalid", false, null, null, "email"));
        when(sensitiveDataMasker.maskRejectedValue("email", "invalid")).thenReturn("invalid");

        final ProblemDetail actual = newHandler()
                .handleBindException(new BindException(bindingResult), newRequest());

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        final List<Map<String, Object>> errors = errors(actual);
        assertThat(errors).singleElement().satisfies(error -> {
            assertThat(error).containsEntry("target", "email");
            assertThat(error).containsEntry("reason", "email");
            assertThat(error).containsEntry("rejectedValue", "invalid");
        });
    }

    @Test
    @DisplayName("GlobalExceptionHandler は ConstraintViolationException を validation error の ProblemDetail へ変換する")
    void should_createValidationProblemDetail_when_constraintViolationExceptionIsHandled() {
        @SuppressWarnings("unchecked")
        final ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        final Path path = mock(Path.class);
        when(path.toString()).thenReturn("create.userName");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be blank");
        when(violation.getInvalidValue()).thenReturn(" ");
        when(sensitiveDataMasker.maskRejectedValue("create.userName", " ")).thenReturn(" ");

        final ProblemDetail actual = newHandler()
                .handleConstraintViolation(new ConstraintViolationException(Set.of(violation)), newRequest());

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        final List<Map<String, Object>> errors = errors(actual);
        assertThat(errors).singleElement().satisfies(error -> {
            assertThat(error).containsEntry("target", "create.userName");
            assertThat(error).containsEntry("reason", "must not be blank");
            assertThat(error).containsEntry("rejectedValue", " ");
        });
    }

    @Test
    @DisplayName("GlobalExceptionHandler は MissingServletRequestParameterException を validation error の ProblemDetail へ変換する")
    void should_createValidationProblemDetail_when_missingRequestParameterExceptionIsHandled() {
        final ProblemDetail actual = newHandler()
                .handleMissingRequestParameter(new MissingServletRequestParameterException("size", "int"), newRequest());

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(actual.getDetail()).isEqualTo("必須パラメータが不足しています。");
        assertThat(errors(actual)).singleElement().satisfies(error ->
                assertThat(error)
                        .containsEntry("target", "size")
                        .containsEntry("reason", "required")
                        .containsEntry("source", "query"));
    }

    @Test
    @DisplayName("GlobalExceptionHandler は MissingServletRequestPartException を validation error の ProblemDetail へ変換する")
    void should_createValidationProblemDetail_when_missingRequestPartExceptionIsHandled() {
        final ProblemDetail actual = newHandler()
                .handleMissingRequestPart(new MissingServletRequestPartException("imageFile"), newRequest());

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(actual.getDetail()).isEqualTo("必須ファイルが不足しています。");
        assertThat(errors(actual)).singleElement().satisfies(error ->
                assertThat(error)
                        .containsEntry("target", "imageFile")
                        .containsEntry("reason", "required")
                        .containsEntry("source", "multipart"));
    }

    @Test
    @DisplayName("GlobalExceptionHandler は HttpMessageNotReadableException を validation error の ProblemDetail へ変換する")
    void should_createValidationProblemDetail_when_httpMessageNotReadableExceptionIsHandled() {
        final HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException("bad json", new IOException("broken body"), dummyHttpInputMessage());

        final ProblemDetail actual = newHandler()
                .handleHttpMessageNotReadable(exception, newRequest());

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(actual.getTitle()).isEqualTo("Validation Error");
        assertThat(errors(actual)).singleElement().satisfies(error -> {
            assertThat(error).containsEntry("target", "requestBody");
            assertThat(error).containsEntry("reason", "not_readable");
            assertThat(error).containsEntry("source", "body");
            assertThat(error).doesNotContainKey("rejectedValue");
        });
    }

    @Test
    @DisplayName("GlobalExceptionHandler は MethodArgumentTypeMismatchException を validation error の ProblemDetail へ変換する")
    void should_createValidationProblemDetail_when_methodArgumentTypeMismatchExceptionIsHandled() throws Exception {
        final MethodArgumentTypeMismatchException exception =
                new MethodArgumentTypeMismatchException("abc", Integer.class, "size", methodParameter(), null);
        when(sensitiveDataMasker.maskRejectedValue("size", "abc")).thenReturn("abc");

        final ProblemDetail actual = newHandler()
                .handleMethodArgumentTypeMismatch(exception, newRequest());

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(actual.getDetail()).isEqualTo("リクエストパラメータの型が不正です。");
        assertThat(errors(actual)).singleElement().satisfies(error -> {
            assertThat(error).containsEntry("target", "size");
            assertThat(error).containsEntry("reason", "type_mismatch");
            assertThat(error).containsEntry("source", "query");
            assertThat(error).containsEntry("rejectedValue", "abc");
        });
    }

    @Test
    @DisplayName("GlobalExceptionHandler は UseCaseExecutionException を対応ステータスの ProblemDetail へ変換する")
    void should_createUseCaseProblemDetail_when_useCaseExecutionExceptionIsHandled() {
        final PostCommandFailedException exception =
                PostCommandFailedException.persistenceFailed(new IOException("db down"));
        when(sensitiveDataMasker.maskMap(exception.getDetails())).thenReturn(exception.getDetails());

        final ProblemDetail actual = newHandler()
                .handleUseCaseExecutionException(exception);

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(actual.getTitle()).isEqualTo("Use Case Error");
        assertThat(actual.getProperties()).containsEntry("errorCode", ErrorCode.POST_COMMAND_FAILED);
        assertThat(castMap(property(actual)))
                .containsEntry("target", "post")
                .containsEntry("reason", "persistence_error")
                .doesNotContainKey("rejectedValue");
    }

    @Test
    @DisplayName("GlobalExceptionHandler は DomainException を対応ステータスの ProblemDetail へ変換する")
    void should_createDomainProblemDetail_when_domainExceptionIsHandled() {
        final InvalidModelException exception = InvalidModelException.invalid("image", "too-big", "total_pixels_exceed_limit");
        when(sensitiveDataMasker.maskMap(exception.getDetails())).thenReturn(exception.getDetails());

        final ProblemDetail actual = newHandler().handleDomainException(exception);

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(actual.getTitle()).isEqualTo("Domain Error");
        assertThat(actual.getProperties()).containsEntry("errorCode", ErrorCode.DOMAIN_INVALID_MODEL);
    }

    @Test
    @DisplayName("GlobalExceptionHandler は ApplicationException を対応ステータスの ProblemDetail へ変換する")
    void should_createApplicationProblemDetail_when_applicationExceptionIsHandled() {
        final ApplicationException exception = new TestApplicationException();
        when(sensitiveDataMasker.maskMap(exception.getDetails())).thenReturn(exception.getDetails());

        final ProblemDetail actual = newHandler().handleApplicationException(exception);

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(actual.getTitle()).isEqualTo("Application Error");
        assertThat(actual.getProperties()).containsEntry("errorCode", ErrorCode.USER_COMMAND_FAILED);
    }

    @Test
    @DisplayName("GlobalExceptionHandler は NoResourceFoundException を 404 の ProblemDetail へ変換する")
    void should_createNotFoundProblemDetail_when_noResourceFoundExceptionIsHandled() {
        final ProblemDetail actual = newHandler()
                .handleNoResourceFound(new NoResourceFoundException(HttpMethod.GET, "/assets/logo.svg", null));

        assertThat(actual.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(actual.getTitle()).isEqualTo("Not Found");
        assertThat(actual.getDetail()).isEqualTo("リソースが見つかりません。");
        assertThat(actual.getProperties()).containsEntry("errorCode", "RESOURCE_NOT_FOUND");
    }

    @Test
    @DisplayName("GlobalExceptionHandler は想定外例外を 500 の ProblemDetail へ変換する")
    void should_createInternalServerErrorProblemDetail_when_unexpectedExceptionIsHandled() {
        final ProblemDetail actual = newHandler().handleException(new RuntimeException("unexpected"));

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

        final ProblemDetail actual = newHandler().handleInvalidValue(exception);

        assertThat(actual.getProperties()).containsEntry("details", maskedDetails);
        verify(sensitiveDataMasker, times(2)).maskMap(exception.getDetails());
    }

    private static MethodParameter methodParameter() throws NoSuchMethodException {
        final Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod("dummy", SignUpRequest.class);
        return new MethodParameter(method, 0);
    }

    private GlobalExceptionHandler newHandler() {
        return new GlobalExceptionHandler(failureEventResolver, sensitiveDataMasker);
    }

    private static MockHttpServletRequest newRequest() {
        return new MockHttpServletRequest();
    }

    private static HttpInputMessage dummyHttpInputMessage() {
        return new HttpInputMessage() {
            @Override
            public @NonNull InputStream getBody() {
                return new ByteArrayInputStream(new byte[0]);
            }

            @Override
            public @NonNull HttpHeaders getHeaders() {
                return HttpHeaders.EMPTY;
            }
        };
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

    private static Object property(final ProblemDetail problemDetail) {
        return Objects.requireNonNull(problemDetail.getProperties()).get("details");
    }

    private static List<Map<String, Object>> errors(final ProblemDetail problemDetail) {
        return castList(castMap(property(problemDetail)).get("errors"));
    }

    private static final class TestApplicationException extends ApplicationException {
        private TestApplicationException() {
            super(ErrorCode.USER_COMMAND_FAILED, "application failed", Map.of("target", "user", "reason", "save_failed"));
        }
    }

}
