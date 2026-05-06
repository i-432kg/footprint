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
        final PostCommandFailedException exception = PostCommandFailedException.imageSaveFailed(cause);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POST_COMMAND_FAILED);
        assertThat(exception.getMessage()).isEqualTo("image.fileName is invalid. reason=image_save_failed");
        assertThat(exception.getDetails())
                .containsEntry("target", "image.fileName")
                .containsEntry("reason", "image_save_failed")
                .doesNotContainKey("rejectedValue");
        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    @DisplayName("PostCommandFailedException.imageMetadataExtractFailed はメタデータ抽出失敗情報を組み立てる")
    void should_buildImageMetadataExtractFailedDetails_when_factoryIsUsed() {
        final IOException cause = new IOException("extract failed");
        final PostCommandFailedException exception =
                PostCommandFailedException.imageMetadataExtractFailed(cause);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POST_COMMAND_FAILED);
        assertThat(exception.getMessage()).isEqualTo("image.objectKey is invalid. reason=image_metadata_extract_failed");
        assertThat(exception.getDetails())
                .containsEntry("target", "image.objectKey")
                .containsEntry("reason", "image_metadata_extract_failed")
                .doesNotContainKey("rejectedValue");
        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    @DisplayName("PostCommandFailedException.persistenceFailed は永続化失敗情報を組み立てる")
    void should_buildPersistenceFailedDetails_when_factoryIsUsed() {
        final IllegalStateException cause = new IllegalStateException("db down");
        final PostCommandFailedException exception = PostCommandFailedException.persistenceFailed(cause);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POST_COMMAND_FAILED);
        assertThat(exception.getMessage()).isEqualTo("post is invalid. reason=persistence_error");
        assertThat(exception.getDetails())
                .containsEntry("target", "post")
                .containsEntry("reason", "persistence_error")
                .doesNotContainKey("rejectedValue");
        assertThat(exception.getCause()).isSameAs(cause);
    }
}
