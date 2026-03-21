package jp.i432kg.footprint.application.exception.usecase;

import jp.i432kg.footprint.exception.ErrorCode;

import java.util.Map;

public class PostCommandFailedException extends UseCaseExecutionException {

    private PostCommandFailedException(String message, Map<String, Object> details, Throwable cause) {
        super(ErrorCode.POST_COMMAND_FAILED, message, details, cause);
    }

    public static PostCommandFailedException imageSaveFailed(Object rejectedValue, Throwable cause) {
        return new PostCommandFailedException(
                message("image", "image_save_failed"),
                details("image", "image_save_failed", rejectedValue),
                cause
        );
    }

    public static PostCommandFailedException imageMetadataExtractFailed(Object rejectedValue, Throwable cause) {
        return new PostCommandFailedException(
                message("image", "image_metadata_extract_failed"),
                details("image", "image_metadata_extract_failed", rejectedValue),
                cause
        );
    }

    public static PostCommandFailedException persistenceFailed(Object rejectedValue, Throwable cause) {
        return new PostCommandFailedException(
                message("post", "persistence_error"),
                details("post", "persistence_error", rejectedValue),
                cause
        );
    }
}