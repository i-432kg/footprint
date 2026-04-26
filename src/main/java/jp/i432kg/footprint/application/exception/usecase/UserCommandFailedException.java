package jp.i432kg.footprint.application.exception.usecase;

import jp.i432kg.footprint.exception.ErrorCode;

import java.util.Map;

/**
 * ユーザー作成ユースケースの実行失敗を表す例外です。
 */
public class UserCommandFailedException extends UseCaseExecutionException {

    private UserCommandFailedException(
            final String message,
            final Map<String, Object> details,
            final Throwable cause
    ) {
        super(ErrorCode.USER_COMMAND_FAILED, message, details, cause);
    }

    /**
     * ユーザー保存失敗を表す例外を生成します。
     *
     * @param rejectedValue 問題となった値
     * @param cause 元になった例外
     * @return 生成した例外
     */
    public static UserCommandFailedException saveFailed(
            final Object rejectedValue,
            final Throwable cause
    ) {
        return new UserCommandFailedException(
                message("user", "save_failed"),
                details("user", "save_failed", rejectedValue),
                cause
        );
    }
}
