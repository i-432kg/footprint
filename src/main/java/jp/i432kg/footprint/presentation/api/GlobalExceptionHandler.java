package jp.i432kg.footprint.presentation.api;

import jakarta.validation.ConstraintViolationException;
import jp.i432kg.footprint.application.exception.ApplicationException;
import jp.i432kg.footprint.application.exception.resource.PostNotFoundException;
import jp.i432kg.footprint.application.exception.resource.ReplyNotFoundException;
import jp.i432kg.footprint.application.exception.resource.UserNotFoundException;
import jp.i432kg.footprint.application.exception.usecase.UseCaseExecutionException;
import jp.i432kg.footprint.domain.exception.DomainException;
import jp.i432kg.footprint.domain.exception.EmailAlreadyUsedException;
import jp.i432kg.footprint.domain.exception.InvalidValueException;
import jp.i432kg.footprint.domain.exception.ReplyPostMismatchException;
import jp.i432kg.footprint.exception.BaseException;
import jp.i432kg.footprint.exception.ErrorCode;
import jp.i432kg.footprint.logging.masking.SensitiveDataMasker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API 全体の例外を {@link ProblemDetail} へ変換する例外ハンドラです。
 * <p>
 * application/domain の独自例外と validation 例外を一貫したレスポンス形式へ変換し、
 * `errorCode` と `details` を含むエラー応答を返します。
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final String ERROR_CODE_DOMAIN_INVALID_VALUE = "DOMAIN_INVALID_VALUE";
    private static final String ERROR_CODE_UNEXPECTED_ERROR = "UNEXPECTED_ERROR";

    private final SensitiveDataMasker sensitiveDataMasker;

    /**
     * 投稿未検出例外を 404 の {@link ProblemDetail} へ変換します。
     */
    @ExceptionHandler(PostNotFoundException.class)
    public ProblemDetail handlePostNotFound(final PostNotFoundException ex) {
        log.warn("Post not found. errorCode={}, details={}", ex.getErrorCode(), maskedDetails(ex));
        return createApplicationProblemDetail(HttpStatus.NOT_FOUND, "Post Not Found", ex);
    }

    /**
     * 返信未検出例外を 404 の {@link ProblemDetail} へ変換します。
     */
    @ExceptionHandler(ReplyNotFoundException.class)
    public ProblemDetail handleReplyNotFound(final ReplyNotFoundException ex) {
        log.warn("Reply not found. errorCode={}, details={}", ex.getErrorCode(), maskedDetails(ex));
        return createApplicationProblemDetail(HttpStatus.NOT_FOUND, "Reply Not Found", ex);
    }

    /**
     * ユーザー未検出例外を 404 の {@link ProblemDetail} へ変換します。
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(final UserNotFoundException ex) {
        log.warn("User not found. errorCode={}, details={}", ex.getErrorCode(), maskedDetails(ex));
        return createApplicationProblemDetail(HttpStatus.NOT_FOUND, "User Not Found", ex);
    }

    /**
     * メール重複例外を 409 の {@link ProblemDetail} へ変換します。
     */
    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ProblemDetail handleEmailAlreadyUsed(final EmailAlreadyUsedException ex) {
        log.warn("Email already used. errorCode={}, details={}", ex.getErrorCode(), maskedDetails(ex));
        return createApplicationProblemDetail(HttpStatus.CONFLICT, "Email Already Used", ex);
    }

    /**
     * 値オブジェクト検証失敗を 400 の {@link ProblemDetail} へ変換します。
     */
    @ExceptionHandler(InvalidValueException.class)
    public ProblemDetail handleInvalidValue(final InvalidValueException ex) {
        log.warn("Invalid value. errorCode={}, details={}", ex.getErrorCode(), maskedDetails(ex));
        return createApplicationProblemDetail(HttpStatus.BAD_REQUEST, "Invalid Value", ex);
    }

    /**
     * 親返信と投稿の不整合例外を 400 の {@link ProblemDetail} へ変換します。
     */
    @ExceptionHandler(ReplyPostMismatchException.class)
    public ProblemDetail handleReplyPostMismatch(final ReplyPostMismatchException ex) {
        log.warn("Reply post mismatch. errorCode={}, details={}", ex.getErrorCode(), maskedDetails(ex));
        return createApplicationProblemDetail(HttpStatus.BAD_REQUEST, "Reply Post Mismatch", ex);
    }

    /**
     * リクエストボディの Bean Validation 失敗を 400 の {@link ProblemDetail} へ変換します。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(final MethodArgumentNotValidException ex) {
        final List<Map<String, Object>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> validationError(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()
                ))
                .toList();

        log.warn("Validation failed. errors={}", errors);
        return createValidationProblemDetail(
                "リクエストの形式が不正です。",
                errors
        );
    }

    /**
     * フォーム・クエリパラメータのバインド失敗を 400 の {@link ProblemDetail} へ変換します。
     */
    @ExceptionHandler(BindException.class)
    public ProblemDetail handleBindException(final BindException ex) {
        final List<Map<String, Object>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> validationError(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()
                ))
                .toList();

        log.warn("Binding failed. errors={}", errors);
        return createValidationProblemDetail(
                "リクエストの形式が不正です。",
                errors
        );
    }

    /**
     * メソッド引数の制約違反を 400 の {@link ProblemDetail} へ変換します。
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(final ConstraintViolationException ex) {
        final List<Map<String, Object>> errors = ex.getConstraintViolations().stream()
                .map(violation -> validationError(
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        violation.getInvalidValue()
                ))
                .toList();

        log.warn("Constraint violation. errors={}", errors);
        return createValidationProblemDetail(
                "リクエストの形式が不正です。",
                errors
        );
    }

    /**
     * 必須リクエストパラメータ欠落を 400 の {@link ProblemDetail} へ変換します。
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleMissingRequestParameter(final MissingServletRequestParameterException ex) {
        final List<Map<String, Object>> errors = List.of(
                validationError(ex.getParameterName(), "required", null, "query")
        );

        log.warn("Missing request parameter. parameter={}", ex.getParameterName());
        return createValidationProblemDetail(
                "必須パラメータが不足しています。",
                errors
        );
    }

    /**
     * 必須 multipart パート欠落を 400 の {@link ProblemDetail} へ変換します。
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ProblemDetail handleMissingRequestPart(final MissingServletRequestPartException ex) {
        final List<Map<String, Object>> errors = List.of(
                validationError(ex.getRequestPartName(), "required", null, "multipart")
        );

        log.warn("Missing request part. part={}", ex.getRequestPartName());
        return createValidationProblemDetail(
                "必須ファイルが不足しています。",
                errors
        );
    }

    /**
     * リクエストボディの JSON 解析失敗を 400 の {@link ProblemDetail} へ変換します。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable(final HttpMessageNotReadableException ex) {
        final List<Map<String, Object>> errors = List.of(
                validationError("requestBody", "not_readable", null, "body")
        );

        log.warn(
                "Request body is not readable. cause={}",
                ex.getMostSpecificCause().getClass().getSimpleName()
        );
        return createValidationProblemDetail(
                "リクエストボディを解析できませんでした。",
                errors
        );
    }

    /**
     * リクエストパラメータの型変換失敗を 400 の {@link ProblemDetail} へ変換します。
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex) {
        final List<Map<String, Object>> errors = List.of(
                validationError(ex.getName(), "type_mismatch", ex.getValue(), "query")
        );

        log.warn("Type mismatch. parameter={}, value={}", ex.getName(), ex.getValue());
        return createValidationProblemDetail(
                "リクエストパラメータの型が不正です。",
                errors
        );
    }

    /**
     * use case 実行失敗例外を、`ErrorCode` に応じた HTTP ステータスの {@link ProblemDetail} へ変換します。
     */
    @ExceptionHandler(UseCaseExecutionException.class)
    public ProblemDetail handleUseCaseExecutionException(final UseCaseExecutionException ex) {
        log.warn("Use case failed. errorCode={}, details={}", ex.getErrorCode(), maskedDetails(ex));
        return createApplicationProblemDetail(resolveStatus(ex), "Use Case Error", ex);
    }

    /**
     * domain 例外を、`ErrorCode` に応じた HTTP ステータスの {@link ProblemDetail} へ変換します。
     */
    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(final DomainException ex) {
        log.warn("Domain error occurred. errorCode={}, details={}", ex.getErrorCode(), maskedDetails(ex));
        return createApplicationProblemDetail(resolveStatus(ex), "Domain Error", ex);
    }

    /**
     * application 例外を、`ErrorCode` に応じた HTTP ステータスの {@link ProblemDetail} へ変換します。
     */
    @ExceptionHandler(ApplicationException.class)
    public ProblemDetail handleApplicationException(final ApplicationException ex) {
        log.warn("Application error occurred. errorCode={}, details={}", ex.getErrorCode(), maskedDetails(ex));
        return createApplicationProblemDetail(resolveStatus(ex), "Application Error", ex);
    }

    /**
     * 想定外例外を 500 の汎用 {@link ProblemDetail} へ変換します。
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(final Exception ex) {
        log.error("Unexpected error occurred.", ex);
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "サーバー内部でエラーが発生しました。"
        );
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("errorCode", ERROR_CODE_UNEXPECTED_ERROR);
        return problemDetail;
    }

    private ProblemDetail createApplicationProblemDetail(
            final HttpStatus status,
            final String title,
            final BaseException ex
    ) {
        return createProblemDetail(status, title, ex);
    }

    /**
     * 独自例外情報を {@link ProblemDetail} に変換します。
     *
     * @param status HTTP ステータス
     * @param title  タイトル
     * @param ex     独自例外
     * @return ProblemDetail
     */
    private ProblemDetail createProblemDetail(
            final HttpStatus status,
            final String title,
            final BaseException ex
    ) {
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle(title);
        problemDetail.setProperty("errorCode", ex.getErrorCode());
        problemDetail.setProperty("details", maskedDetails(ex));
        return problemDetail;
    }

    /**
     * バリデーションエラーの ProblemDetail を生成します。
     *
     * @param detail detail
     * @param errors エラー一覧
     * @return ProblemDetail
     */
    private ProblemDetail createValidationProblemDetail(
            final String detail,
            final List<Map<String, Object>> errors
    ) {
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problemDetail.setTitle("Validation Error");
        problemDetail.setProperty("errorCode", ERROR_CODE_DOMAIN_INVALID_VALUE);
        problemDetail.setProperty("details", Map.of("errors", errors));
        return problemDetail;
    }

    /**
     * バリデーションエラー1件分の情報を作成します。
     *
     * @param target 入力対象
     * @param reason エラー理由
     * @param rejectedValue 入力値
     * @return エラー情報
     */
    private Map<String, Object> validationError(
            final String target,
            final String reason,
            final Object rejectedValue
    ) {
        return validationError(target, reason, rejectedValue, null);
    }

    private Map<String, Object> validationError(
            final String target,
            final String reason,
            final Object rejectedValue,
            final String source
    ) {
        final Map<String, Object> error = new LinkedHashMap<>();
        error.put("target", target);
        error.put("reason", reason);
        if (source != null) {
            error.put("source", source);
        }
        if (rejectedValue != null) {
            error.put("rejectedValue", sanitizeRejectedValue(target, rejectedValue));
        }
        return error;
    }

    private Map<String, Object> maskedDetails(final BaseException ex) {
        return sensitiveDataMasker.maskMap(ex.getDetails());
    }

    private Object sanitizeRejectedValue(final String field, final Object rejectedValue) {
        return sensitiveDataMasker.maskRejectedValue(field, rejectedValue);
    }

    /**
     * 独自例外の {@link ErrorCode} に応じて HTTP ステータスを決定します。
     *
     * @param ex 独自例外
     * @return HTTP ステータス
     */
    private HttpStatus resolveStatus(final BaseException ex) {
        return switch (ex.getErrorCode()) {
            case POST_NOT_FOUND, REPLY_NOT_FOUND, USER_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case EMAIL_ALREADY_USED -> HttpStatus.CONFLICT;
            case REPLY_POST_MISMATCH, DOMAIN_INVALID_VALUE, DOMAIN_INVALID_MODEL -> HttpStatus.BAD_REQUEST;
            case POST_COMMAND_FAILED, REPLY_COMMAND_FAILED, USER_COMMAND_FAILED, FILE_STORAGE_ERROR, PERSISTENCE_ERROR,
                 UNEXPECTED_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
