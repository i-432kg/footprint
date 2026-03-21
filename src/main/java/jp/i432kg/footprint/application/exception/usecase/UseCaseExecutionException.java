package jp.i432kg.footprint.application.exception.usecase;

import jp.i432kg.footprint.application.exception.ApplicationException;
import jp.i432kg.footprint.exception.ErrorCode;

import java.util.Map;

public abstract class UseCaseExecutionException extends ApplicationException {

    protected UseCaseExecutionException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }

    protected UseCaseExecutionException(ErrorCode errorCode, String message, Map<String, Object> details, Throwable cause) {
        super(errorCode, message, details, cause);
    }
}