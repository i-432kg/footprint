package jp.i432kg.footprint.application.exception.usecase;

import jp.i432kg.footprint.exception.ErrorCode;

import java.util.Map;

/**
 * 投稿作成ユースケースの実行失敗を表す例外です。
 */
public class PostCommandFailedException extends UseCaseExecutionException {

    private PostCommandFailedException(
            final String message,
            final Map<String, Object> details,
            final Throwable cause
    ) {
        super(ErrorCode.POST_COMMAND_FAILED, message, details, cause);
    }

    /**
     * 画像保存失敗を表す例外を生成します。
     *
     * @param rejectedValue 問題となった値
     * @param cause 元になった例外
     * @return 生成した例外
     */
    public static PostCommandFailedException imageSaveFailed(
            final Object rejectedValue,
            final Throwable cause
    ) {
        return new PostCommandFailedException(
                message("image", "image_save_failed"),
                details("image", "image_save_failed", rejectedValue),
                cause
        );
    }

    /**
     * 画像メタデータ抽出失敗を表す例外を生成します。
     *
     * @param rejectedValue 問題となった値
     * @param cause 元になった例外
     * @return 生成した例外
     */
    public static PostCommandFailedException imageMetadataExtractFailed(
            final Object rejectedValue,
            final Throwable cause
    ) {
        return new PostCommandFailedException(
                message("image", "image_metadata_extract_failed"),
                details("image", "image_metadata_extract_failed", rejectedValue),
                cause
        );
    }

    /**
     * 投稿永続化失敗を表す例外を生成します。
     *
     * @param rejectedValue 問題となった値
     * @param cause 元になった例外
     * @return 生成した例外
     */
    public static PostCommandFailedException persistenceFailed(
            final Object rejectedValue,
            final Throwable cause
    ) {
        return new PostCommandFailedException(
                message("post", "persistence_error"),
                details("post", "persistence_error", rejectedValue),
                cause
        );
    }
}
