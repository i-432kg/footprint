package jp.i432kg.footprint.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * details を持つ例外の共通基底
 */
public abstract class DetailBasedException extends BaseException {

    protected static final String KEY_TARGET = "target";
    protected static final String KEY_REASON = "reason";
    protected static final String KEY_REJECTED_VALUE = "rejectedValue";

    protected DetailBasedException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }

    protected DetailBasedException(ErrorCode errorCode, String message, Map<String, Object> details, Throwable cause) {
        super(errorCode, message, details, cause);
    }

    protected static String message(String target, String reason) {
        return target + " is invalid. reason=" + reason;
    }

    protected static Map<String, Object> details(String target, String reason, Object rejectedValue) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put(KEY_TARGET, Objects.requireNonNull(target));
        details.put(KEY_REASON, Objects.requireNonNull(reason));
        if (Objects.nonNull(rejectedValue)) {
            details.put(KEY_REJECTED_VALUE, rejectedValue);
        }
        return Map.copyOf(details);
    }

    protected static Map<String, Object> details(
            String target,
            String reason,
            Object rejectedValue,
            Map<String, Object> extra
    ) {
        Map<String, Object> details = new LinkedHashMap<>(details(target, reason, rejectedValue));
        if (Objects.nonNull(extra)) {
            details.putAll(extra);
        }
        return Map.copyOf(details);
    }
}