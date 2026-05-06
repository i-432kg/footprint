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
     * @param cause 元になった例外
     * @return 生成した例外
     */
    public static PostCommandFailedException imageSaveFailed(final Throwable cause) {
        return new PostCommandFailedException(
                message("image.fileName", "image_save_failed"),
                details("image.fileName", "image_save_failed"),
                cause
        );
    }

    /**
     * 画像メタデータ抽出失敗を表す例外を生成します。
     *
     * @param cause 元になった例外
     * @return 生成した例外
     */
    public static PostCommandFailedException imageMetadataExtractFailed(final Throwable cause) {
        return new PostCommandFailedException(
                message("image.objectKey", "image_metadata_extract_failed"),
                details("image.objectKey", "image_metadata_extract_failed"),
                cause
        );
    }

    /**
     * 投稿永続化失敗を表す例外を生成します。
     *
     * @param cause 元になった例外
     * @return 生成した例外
     */
    public static PostCommandFailedException persistenceFailed(final Throwable cause) {
        return new PostCommandFailedException(
                message("post", "persistence_error"),
                details("post", "persistence_error"),
                cause
        );
    }
}
