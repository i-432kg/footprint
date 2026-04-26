package jp.i432kg.footprint.application.exception.usecase;

import jp.i432kg.footprint.exception.ErrorCode;

import java.util.Map;

/**
 * 返信作成ユースケースの実行失敗を表す例外です。
 */
public class ReplyCommandFailedException extends UseCaseExecutionException {

    private ReplyCommandFailedException(
            final String message,
            final Map<String, Object> details,
            final Throwable cause
    ) {
        super(ErrorCode.REPLY_COMMAND_FAILED, message, details, cause);
    }

    /**
     * 返信保存失敗を表す例外を生成します。
     *
     * @param rejectedValue 問題となった値
     * @param cause 元になった例外
     * @return 生成した例外
     */
    public static ReplyCommandFailedException saveFailed(
            final Object rejectedValue,
            final Throwable cause
    ) {
        return new ReplyCommandFailedException(
                message("reply", "save_failed"),
                details("reply", "save_failed", rejectedValue),
                cause
        );
    }

    /**
     * 親返信の childCount 加算失敗を表す例外を生成します。
     *
     * @param rejectedValue 問題となった値
     * @param cause 元になった例外
     * @return 生成した例外
     */
    public static ReplyCommandFailedException increaseReplyCountFailed(
            final Object rejectedValue,
            final Throwable cause
    ) {
        return new ReplyCommandFailedException(
                message("reply", "increase_reply_count_failed"),
                details("reply", "increase_reply_count_failed", rejectedValue),
                cause
        );
    }
}
