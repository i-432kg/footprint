package jp.i432kg.footprint.application.exception;

import jp.i432kg.footprint.exception.DetailBasedException;
import jp.i432kg.footprint.exception.ErrorCode;

import java.util.Map;

public abstract class ApplicationException extends DetailBasedException {

    protected ApplicationException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }

    protected ApplicationException(ErrorCode errorCode, String message, Map<String, Object> details, Throwable cause) {
        super(errorCode, message, details, cause);
    }
}