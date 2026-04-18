package jp.i432kg.footprint.application.exception.usecase;

import jp.i432kg.footprint.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class PostCommandFailedExceptionTest {

    @Test
    @DisplayName("PostCommandFailedException.imageSaveFailed は画像保存失敗情報を組み立てる")
    void should_buildImageSaveFailedDetails_when_factoryIsUsed() {
        final IOException cause = new IOException("save failed");
        final PostCommandFailedException exception = PostCommandFailedException.imageSaveFailed("image.jpg", cause);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POST_COMMAND_FAILED);
        assertThat(exception.getMessage()).isEqualTo("image is invalid. reason=image_save_failed");
        assertThat(exception.getDetails())
                .containsEntry("target", "image")
                .containsEntry("reason", "image_save_failed")
                .containsEntry("rejectedValue", "image.jpg");
        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    @DisplayName("PostCommandFailedException.imageMetadataExtractFailed はメタデータ抽出失敗情報を組み立てる")
    void should_buildImageMetadataExtractFailedDetails_when_factoryIsUsed() {
        final IOException cause = new IOException("extract failed");
        final PostCommandFailedException exception =
                PostCommandFailedException.imageMetadataExtractFailed("users/u/posts/p/images/i.jpg", cause);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POST_COMMAND_FAILED);
        assertThat(exception.getMessage()).isEqualTo("image is invalid. reason=image_metadata_extract_failed");
        assertThat(exception.getDetails())
                .containsEntry("target", "image")
                .containsEntry("reason", "image_metadata_extract_failed")
                .containsEntry("rejectedValue", "users/u/posts/p/images/i.jpg");
        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    @DisplayName("PostCommandFailedException.persistenceFailed は永続化失敗情報を組み立てる")
    void should_buildPersistenceFailedDetails_when_factoryIsUsed() {
        final IllegalStateException cause = new IllegalStateException("db down");
        final PostCommandFailedException exception = PostCommandFailedException.persistenceFailed("01ARZ3NDEKTSV4RRFFQ69G5FAX", cause);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POST_COMMAND_FAILED);
        assertThat(exception.getMessage()).isEqualTo("post is invalid. reason=persistence_error");
        assertThat(exception.getDetails())
                .containsEntry("target", "post")
                .containsEntry("reason", "persistence_error")
                .containsEntry("rejectedValue", "01ARZ3NDEKTSV4RRFFQ69G5FAX");
        assertThat(exception.getCause()).isSameAs(cause);
    }
}
