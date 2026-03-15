package jp.i432kg.footprint.presentation.api;

import jp.i432kg.footprint.domain.exception.DomainException;
import jp.i432kg.footprint.domain.exception.PostNotFoundException;
import jp.i432kg.footprint.domain.exception.ReplyNotFoundException;
import jp.i432kg.footprint.domain.exception.ReplyPostMismatchException;
import jp.i432kg.footprint.domain.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * API 全体の例外をハンドリングする ControllerAdvice
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 投稿が見つからない場合の例外を処理します。
     *
     * @param ex 例外
     * @return ProblemDetail
     */
    @ExceptionHandler(PostNotFoundException.class)
    public ProblemDetail handlePostNotFound(final PostNotFoundException ex) {
        return createProblemDetail(HttpStatus.NOT_FOUND, "Post Not Found", ex.getMessage());
    }

    /**
     * 返信が見つからない場合の例外を処理します。
     *
     * @param ex 例外
     * @return ProblemDetail
     */
    @ExceptionHandler(ReplyNotFoundException.class)
    public ProblemDetail handleReplyNotFound(final ReplyNotFoundException ex) {
        return createProblemDetail(HttpStatus.NOT_FOUND, "Reply Not Found", ex.getMessage());
    }

    /**
     * ユーザーが見つからない場合の例外を処理します。
     *
     * @param ex 例外
     * @return ProblemDetail
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(final UserNotFoundException ex) {
        return createProblemDetail(HttpStatus.NOT_FOUND, "User Not Found", ex.getMessage());
    }

    /**
     * 親返信と投稿の不整合がある場合の例外を処理します。
     *
     * @param ex 例外
     * @return ProblemDetail
     */
    @ExceptionHandler(ReplyPostMismatchException.class)
    public ProblemDetail handleReplyPostMismatch(final ReplyPostMismatchException ex) {
        return createProblemDetail(HttpStatus.BAD_REQUEST, "Reply Post Mismatch", ex.getMessage());
    }

    /**
     * ドメイン例外を処理します。
     *
     * @param ex 例外
     * @return ProblemDetail
     */
    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(final DomainException ex) {
        return createProblemDetail(HttpStatus.BAD_REQUEST, "Domain Error", ex.getMessage());
    }

    /**
     * 想定外の例外を処理します。
     *
     * @param ex 例外
     * @return ProblemDetail
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(final Exception ex) {
        log.error("e: ", ex);
        return createProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "サーバー内部でエラーが発生しました。"
        );
    }

    private ProblemDetail createProblemDetail(
            final HttpStatus status,
            final String title,
            final String detail
    ) {
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        return problemDetail;
    }
}