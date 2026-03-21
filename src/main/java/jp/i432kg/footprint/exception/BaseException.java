package jp.i432kg.footprint.exception;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * 共通例外基底クラス
 */
public abstract class BaseException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    protected BaseException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = Objects.requireNonNull(errorCode);
        this.details = Collections.emptyMap();
    }

    protected BaseException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(message);
        this.errorCode = Objects.requireNonNull(errorCode);
        this.details = Objects.isNull(details) ? Collections.emptyMap() : Map.copyOf(details);
    }

    protected BaseException(ErrorCode errorCode, String message, Map<String, Object> details, Throwable cause) {
        super(message, cause);
        this.errorCode = Objects.requireNonNull(errorCode);
        this.details = Objects.isNull(details) ? Collections.emptyMap() : Map.copyOf(details);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}