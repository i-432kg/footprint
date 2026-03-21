package jp.i432kg.footprint.application.exception.usecase;

import jp.i432kg.footprint.exception.ErrorCode;

import java.util.Map;

public class UserCommandFailedException extends UseCaseExecutionException {

    private UserCommandFailedException(String message, Map<String, Object> details, Throwable cause) {
        super(ErrorCode.USER_COMMAND_FAILED, message, details, cause);
    }

    public static UserCommandFailedException saveFailed(Object rejectedValue, Throwable cause) {
        return new UserCommandFailedException(
                message("user", "save_failed"),
                details("user", "save_failed", rejectedValue),
                cause
        );
    }
}