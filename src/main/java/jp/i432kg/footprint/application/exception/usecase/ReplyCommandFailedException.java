package jp.i432kg.footprint.application.exception.usecase;

import jp.i432kg.footprint.exception.ErrorCode;

import java.util.Map;

public class ReplyCommandFailedException extends UseCaseExecutionException {

    private ReplyCommandFailedException(String message, Map<String, Object> details, Throwable cause) {
        super(ErrorCode.REPLY_COMMAND_FAILED, message, details, cause);
    }

    public static ReplyCommandFailedException saveFailed(Object rejectedValue, Throwable cause) {
        return new ReplyCommandFailedException(
                message("reply", "save_failed"),
                details("reply", "save_failed", rejectedValue),
                cause
        );
    }

    public static ReplyCommandFailedException increaseReplyCountFailed(Object rejectedValue, Throwable cause) {
        return new ReplyCommandFailedException(
                message("reply", "increase_reply_count_failed"),
                details("reply", "increase_reply_count_failed", rejectedValue),
                cause
        );
    }
}