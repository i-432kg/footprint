package jp.i432kg.footprint.logging.operation;

import jakarta.servlet.http.HttpServletRequest;
import jp.i432kg.footprint.logging.LoggingEvents;
import jp.i432kg.footprint.logging.LoggingOperations;
import jp.i432kg.footprint.logging.access.AccessLogContext;
import jp.i432kg.footprint.logging.access.AccessLogFilter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;

/**
 * request に設定された operation と例外種別から failure / warning 系ログ event を解決します。
 */
@Component
public class FailureEventResolver {

    private static final String FIELD_TARGET = "target";
    private static final String FIELD_IMAGE_FILE = "imageFile";

    /**
     * validation 系例外に対応するログ event を解決します。
     *
     * @param request 現在処理中の HTTP リクエスト
     * @param exception 発生した validation 系例外
     * @param errors `GlobalExceptionHandler` が整形した validation error 一覧
     * @return ログへ出力する event 名
     */
    public String resolveValidationEvent(
            final HttpServletRequest request,
            final Exception exception,
            final List<Map<String, Object>> errors
    ) {
        final String operation = AccessLogFilter.findContext(request)
                .flatMap(AccessLogContext::operation)
                .orElse("");

        return switch (operation) {
            case LoggingOperations.POST_CREATE -> resolvePostCreateEvent(exception, errors);
            case LoggingOperations.REPLY_CREATE -> LoggingEvents.REPLY_CREATE_VALIDATION_FAIL;
            case LoggingOperations.POST_TIMELINE_FETCH,
                    LoggingOperations.POST_SEARCH_FETCH,
                    LoggingOperations.ME_POSTS_FETCH -> resolvePostPagingEvent(exception);
            case LoggingOperations.ME_REPLIES_FETCH -> resolveReplyPagingEvent(exception);
            default -> LoggingEvents.REQUEST_VALIDATION_FAIL;
        };
    }

    private String resolvePostCreateEvent(
            final Exception exception,
            final List<Map<String, Object>> errors
    ) {
        return switch (exception) {
            // multipart パート自体が不足している場合はアップロード拒否として扱う
            case MissingServletRequestPartException ignored -> LoggingEvents.POST_CREATE_UPLOAD_REJECTED;

            // Bean Validation / bind 失敗では、imageFile 起因かどうかで upload rejected と validation fail を分ける
            case BindException ignored -> containsTarget(errors)
                    ? LoggingEvents.POST_CREATE_UPLOAD_REJECTED
                    : LoggingEvents.POST_CREATE_VALIDATION_FAIL;

            // request body 自体を解釈できない場合は通常の投稿作成バリデーション失敗として扱う
            case HttpMessageNotReadableException ignored -> LoggingEvents.POST_CREATE_VALIDATION_FAIL;

            // ここまでで特定できない投稿作成系 validation 失敗は汎用の validation fail へ寄せる
            default -> LoggingEvents.POST_CREATE_VALIDATION_FAIL;
        };
    }

    private String resolvePostPagingEvent(final Exception exception) {
        return isPagingValidationException(exception)
                ? LoggingEvents.POST_LAST_ID_INVALID
                : LoggingEvents.REQUEST_VALIDATION_FAIL;
    }

    private String resolveReplyPagingEvent(final Exception exception) {
        return isPagingValidationException(exception)
                ? LoggingEvents.REPLY_LAST_ID_INVALID
                : LoggingEvents.REQUEST_VALIDATION_FAIL;
    }

    private boolean isPagingValidationException(final Exception exception) {
        return exception instanceof ConstraintViolationException
                || exception instanceof MethodArgumentTypeMismatchException
                || exception instanceof BindException;
    }

    private boolean containsTarget(final List<Map<String, Object>> errors) {
        return errors.stream()
                .map(error -> error.get(FIELD_TARGET))
                .anyMatch(FailureEventResolver.FIELD_IMAGE_FILE::equals);
    }
}
