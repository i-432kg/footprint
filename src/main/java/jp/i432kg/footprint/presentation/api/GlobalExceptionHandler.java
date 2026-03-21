package jp.i432kg.footprint.presentation.api;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
public class GlobalExceptionHandler {

    /**
     * 投稿が見つからない場合の例外を処理します。
     *
     * @param ex 投稿未検出例外
     * @return ProblemDetail
     */
    @ExceptionHandler(PostNotFoundException.class)
    public ProblemDetail handlePostNotFound(final PostNotFoundException ex) {
        return createProblemDetail(HttpStatus.NOT_FOUND, "Post Not Found", ex);
    }

    /**
     * 返信が見つからない場合の例外を処理します。
     *
     * @param ex 返信未検出例外
     * @return ProblemDetail
     */
    @ExceptionHandler(ReplyNotFoundException.class)
    public ProblemDetail handleReplyNotFound(final ReplyNotFoundException ex) {
        return createProblemDetail(HttpStatus.NOT_FOUND, "Reply Not Found", ex);
    }

    /**
     * ユーザーが見つからない場合の例外を処理します。
     *
     * @param ex ユーザー未検出例外
     * @return ProblemDetail
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(final UserNotFoundException ex) {
        return createProblemDetail(HttpStatus.NOT_FOUND, "User Not Found", ex);
    }

    /**
     * メールアドレスが既に使用されている場合の例外を処理します。
     *
     * @param ex メール重複例外
     * @return ProblemDetail
     */
    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ProblemDetail handleEmailAlreadyUsed(final EmailAlreadyUsedException ex) {
        return createProblemDetail(HttpStatus.CONFLICT, "Email Already Used", ex);
    }

    /**
     * 値オブジェクトの検証失敗を処理します。
     *
     * @param ex 値オブジェクト検証失敗例外
     * @return ProblemDetail
     */
    @ExceptionHandler(InvalidValueException.class)
    public ProblemDetail handleInvalidValue(final InvalidValueException ex) {
        return createProblemDetail(HttpStatus.BAD_REQUEST, "Invalid Value", ex);
    }

    /**
     * 親返信と投稿の不整合がある場合の例外を処理します。
     *
     * @param ex 親返信と投稿の不整合例外
     * @return ProblemDetail
     */
    @ExceptionHandler(ReplyPostMismatchException.class)
    public ProblemDetail handleReplyPostMismatch(final ReplyPostMismatchException ex) {
        return createProblemDetail(HttpStatus.BAD_REQUEST, "Reply Post Mismatch", ex);
    }

    /**
     * ユースケース実行時の例外を処理します。
     *
     * @param ex ユースケース実行例外
     * @return ProblemDetail
     */
    @ExceptionHandler(UseCaseExecutionException.class)
    public ProblemDetail handleUseCaseExecutionException(final UseCaseExecutionException ex) {
        return createProblemDetail(resolveStatus(ex), "Use Case Error", ex);
    }

    /**
     * ドメイン例外を処理します。
     *
     * @param ex ドメイン例外
     * @return ProblemDetail
     */
    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(final DomainException ex) {
        return createProblemDetail(resolveStatus(ex), "Domain Error", ex);
    }

    /**
     * アプリケーション例外を処理します。
     *
     * @param ex アプリケーション例外
     * @return ProblemDetail
     */
    @ExceptionHandler(ApplicationException.class)
    public ProblemDetail handleApplicationException(final ApplicationException ex) {
        return createProblemDetail(resolveStatus(ex), "Application Error", ex);
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
        problemDetail.setProperty("errorCode", "UNEXPECTED_ERROR");
        return problemDetail;
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
        problemDetail.setProperty("details", ex.getDetails());
        return problemDetail;
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
            case REPLY_POST_MISMATCH, DOMAIN_INVALID_VALUE -> HttpStatus.BAD_REQUEST;
            case POST_COMMAND_FAILED, REPLY_COMMAND_FAILED, USER_COMMAND_FAILED -> HttpStatus.INTERNAL_SERVER_ERROR;
            case FILE_STORAGE_ERROR, PERSISTENCE_ERROR, UNEXPECTED_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}