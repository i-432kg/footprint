package jp.i432kg.footprint.application.exception.resource;

import jp.i432kg.footprint.application.exception.ApplicationException;
import jp.i432kg.footprint.exception.ErrorCode;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public abstract class ResourceNotFoundException extends ApplicationException {

    protected ResourceNotFoundException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }

    protected static Map<String, Object> details(final String target, final String resourceId) {
        final Map<String, Object> details = new LinkedHashMap<>();
        details.put("target", Objects.requireNonNull(target));
        details.put("reason", "not_found");
        details.put("resourceId", Objects.requireNonNull(resourceId));
        return Map.copyOf(details);
    }
}
