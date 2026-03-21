package jp.i432kg.footprint.application.exception.resource;

import jp.i432kg.footprint.application.exception.ApplicationException;
import jp.i432kg.footprint.exception.ErrorCode;

import java.util.Map;

public abstract class ResourceNotFoundException extends ApplicationException {

    protected ResourceNotFoundException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
}