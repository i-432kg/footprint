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
 * API 全体の例外をハンドリングする ControllerAdvice。
 * <p>
 * 独自例外を HTTP ステータスおよび {@link ProblemDetail} に変換し、
 * クライアントへ一貫したエラー応答を返します。
 * </p>
 *
 * <p>
 * 返却する ProblemDetail には以下を含めます。
 * </p>
 * <ul>
 *   <li>HTTP ステータス</li>
 *   <li>title</li>
 *   <li>detail</li>
 *   <li>errorCode</li>
 *   <li>details</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final String ERROR_CODE_DOMAIN_INVALID_VALUE = "DOMAIN_INVALID_VALUE";
    private static final String ERROR_CODE_UNEXPECTED_ERROR = "UNEXPECTED_ERROR";

    private final SensitiveDataMasker sensitiveDataMasker;

    /**
     * 投稿が見つからない場合の例外を処理します。
     *
     * @param ex 投稿未検出例外
     * @return ProblemDetail
     */
    @ExceptionHandler(PostNotFoundException.class)
    public ProblemDetail handlePostNotFound(final PostNotFoundException ex) {
        log.warn("Post not found. errorCode={}, details={}", ex.getErrorCode(), maskedDetails(ex));
        return createApplicationProblemDetail(HttpStatus.NOT_FOUND, "Post Not Found", ex);
    }

    /**
     * 返信が見つからない場合の例外を処理します。
     *
     * @param ex 返信未検出例外
     * @return ProblemDetail
     */
    @ExceptionHandler(ReplyNotFoundException.class)
    public ProblemDetail handleReplyNotFound(final ReplyNotFoundException ex) {
        log.warn("Reply not found. errorCode={}, details={}", ex.getErrorCode(), maskedDetails(ex));
        return createApplicationProblemDetail(HttpStatus.NOT_FOUND, "Reply Not Found", ex);
    }

    /**
     * ユーザーが見つからない場合の例外を処理します。
     *
     * @param ex ユーザー未検出例外
     * @return ProblemDetail
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(final UserNotFoundException ex) {
        log.warn("User not found. errorCode={}, details={}", ex.getErrorCode(), maskedDetails(ex));
        return createApplicationProblemDetail(HttpStatus.NOT_FOUND, "User Not Found", ex);
    }

    /**
     * メールアドレスが既に使用されている場合の例外を処理します。
     *
     * @param ex メール重複例外
     * @return ProblemDetail
     */
    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ProblemDetail handleEmailAlreadyUsed(final EmailAlreadyUsedException ex) {
        log.warn("Email already used. errorCode={}, details={}", ex.getErrorCode(), maskedDetails(ex));
        return createApplicationProblemDetail(HttpStatus.CONFLICT, "Email Already Used", ex);
    }

    /**
     * 値オブジェクトの検証失敗を処理します。
     *
     * @param ex 値オブジェクト検証失敗例外
     * @return ProblemDetail
     */
    @ExceptionHandler(InvalidValueException.class)
    public ProblemDetail handleInvalidValue(final InvalidValueException ex) {
        log.warn("Invalid value. errorCode={}, details={}", ex.getErrorCode(), maskedDetails(ex));
        return createApplicationProblemDetail(HttpStatus.BAD_REQUEST, "Invalid Value", ex);
    }

    /**
     * 親返信と投稿の不整合がある場合の例外を処理します。
     *
     * @param ex 親返信と投稿の不整合例外
     * @return ProblemDetail
     */
    @ExceptionHandler(ReplyPostMismatchException.class)
    public ProblemDetail handleReplyPostMismatch(final ReplyPostMismatchException ex) {
        log.warn("Reply post mismatch. errorCode={}, details={}", ex.getErrorCode(), maskedDetails(ex));
        return createApplicationProblemDetail(HttpStatus.BAD_REQUEST, "Reply Post Mismatch", ex);
    }

    /**
     * リクエストボディのバリデーション失敗を処理します。
     *
     * @param ex リクエストボディ検証失敗例外
     * @return ProblemDetail
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
                "Validation Error",
                "リクエストの形式が不正です。",
                errors
        );
    }

    /**
     * フォーム/クエリのバインド失敗を処理します。
     *
     * @param ex バインド失敗例外
     * @return ProblemDetail
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
                "Validation Error",
                "リクエストの形式が不正です。",
                errors
        );
    }

    /**
     * パラメータ制約違反を処理します。
     *
     * @param ex 制約違反例外
     * @return ProblemDetail
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
                "Validation Error",
                "リクエストの形式が不正です。",
                errors
        );
    }

    /**
     * 必須リクエストパラメータ欠落を処理します。
     *
     * @param ex 欠落例外
     * @return ProblemDetail
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleMissingRequestParameter(final MissingServletRequestParameterException ex) {
        final List<Map<String, Object>> errors = List.of(
                validationError(ex.getParameterName(), "required", null)
        );

        log.warn("Missing request parameter. parameter={}", ex.getParameterName());
        return createValidationProblemDetail(
                "Validation Error",
                "必須パラメータが不足しています。",
                errors
        );
    }

    /**
     * 必須リクエストパート欠落を処理します。
     *
     * @param ex 欠落例外
     * @return ProblemDetail
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ProblemDetail handleMissingRequestPart(final MissingServletRequestPartException ex) {
        final List<Map<String, Object>> errors = List.of(
                validationError(ex.getRequestPartName(), "required", null)
        );

        log.warn("Missing request part. part={}", ex.getRequestPartName());
        return createValidationProblemDetail(
                "Validation Error",
                "必須ファイルが不足しています。",
                errors
        );
    }

    /**
     * リクエストの JSON 解析失敗を処理します。
     *
     * @param ex JSON 解析失敗例外
     * @return ProblemDetail
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable(final HttpMessageNotReadableException ex) {
        final List<Map<String, Object>> errors = List.of(
                validationError("requestBody", "not readable", null)
        );

        log.warn("Request body is not readable.");
        return createValidationProblemDetail(
                "Validation Error",
                "リクエストボディを解析できませんでした。",
                errors
        );
    }

    /**
     * 型不一致によるパラメータ変換失敗を処理します。
     *
     * @param ex 型不一致例外
     * @return ProblemDetail
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex) {
        final List<Map<String, Object>> errors = List.of(
                validationError(ex.getName(), "type mismatch", ex.getValue())
        );

        log.warn("Type mismatch. parameter={}, value={}", ex.getName(), ex.getValue());
        return createValidationProblemDetail(
                "Validation Error",
                "リクエストパラメータの型が不正です。",
                errors
        );
    }

    /**
     * ユースケース実行時の例外を処理します。
     *
     * @param ex ユースケース実行例外
     * @return ProblemDetail
     */
    @ExceptionHandler(UseCaseExecutionException.class)
    public ProblemDetail handleUseCaseExecutionException(final UseCaseExecutionException ex) {
        log.warn("Use case failed. errorCode={}, details={}", ex.getErrorCode(), maskedDetails(ex));
        return createApplicationProblemDetail(resolveStatus(ex), "Use Case Error", ex);
    }

    /**
     * ドメイン例外を処理します。
     *
     * @param ex ドメイン例外
     * @return ProblemDetail
     */
    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(final DomainException ex) {
        log.warn("Domain error occurred. errorCode={}, details={}", ex.getErrorCode(), maskedDetails(ex));
        return createApplicationProblemDetail(resolveStatus(ex), "Domain Error", ex);
    }

    /**
     * アプリケーション例外を処理します。
     *
     * @param ex アプリケーション例外
     * @return ProblemDetail
     */
    @ExceptionHandler(ApplicationException.class)
    public ProblemDetail handleApplicationException(final ApplicationException ex) {
        log.warn("Application error occurred. errorCode={}, details={}", ex.getErrorCode(), maskedDetails(ex));
        return createApplicationProblemDetail(resolveStatus(ex), "Application Error", ex);
    }

    /**
     * 想定外の例外を処理します。
     *
     * @param ex 想定外例外
     * @return ProblemDetail
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
     * @param title title
     * @param detail detail
     * @param errors エラー一覧
     * @return ProblemDetail
     */
    private ProblemDetail createValidationProblemDetail(
            final String title,
            final String detail,
            final List<Map<String, Object>> errors
    ) {
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problemDetail.setTitle(title);
        problemDetail.setProperty("errorCode", ERROR_CODE_DOMAIN_INVALID_VALUE);
        problemDetail.setProperty("details", Map.of("errors", errors));
        return problemDetail;
    }

    /**
     * バリデーションエラー1件分の情報を作成します。
     *
     * @param field フィールド名
     * @param message エラーメッセージ
     * @param rejectedValue 入力値
     * @return エラー情報
     */
    private Map<String, Object> validationError(
            final String field,
            final String message,
            final Object rejectedValue
    ) {
        final Map<String, Object> error = new LinkedHashMap<>();
        error.put("field", field);
        error.put("message", message);
        error.put("rejectedValue", sanitizeRejectedValue(field, rejectedValue));
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
            case POST_COMMAND_FAILED, REPLY_COMMAND_FAILED, USER_COMMAND_FAILED -> HttpStatus.INTERNAL_SERVER_ERROR;
            case FILE_STORAGE_ERROR, PERSISTENCE_ERROR, UNEXPECTED_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
