package jp.i432kg.footprint.application.query.model;

import jp.i432kg.footprint.domain.value.ObjectKey;
import jp.i432kg.footprint.domain.value.StorageType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImageSummaryTest {

    @Test
    @DisplayName("ImageSummary は設定された画像参照情報を保持する")
    void should_holdValues_when_allFieldsAreSet() {
        final ImageSummary summary = new ImageSummary(
                "image-01",
                1,
                StorageType.LOCAL,
                ObjectKey.of("users/user-01/posts/post-01/images/image-01.jpg"),
                "jpg",
                2048L,
                1920,
                1080
        );

        assertThat(summary.getId()).isEqualTo("image-01");
        assertThat(summary.getSortOrder()).isEqualTo(1);
        assertThat(summary.getStorageType()).isEqualTo(StorageType.LOCAL);
        assertThat(summary.getObjectKey()).isEqualTo(ObjectKey.of("users/user-01/posts/post-01/images/image-01.jpg"));
        assertThat(summary.getFileExtension()).isEqualTo("jpg");
        assertThat(summary.getSizeBytes()).isEqualTo(2048L);
        assertThat(summary.getWidth()).isEqualTo(1920);
        assertThat(summary.getHeight()).isEqualTo(1080);
    }

    @Test
    @DisplayName("ImageSummary は no-args 生成時に既定値で初期化される")
    void should_initializeDefaultValues_when_createdWithNoArgsConstructor() {
        final ImageSummary summary = new ImageSummary();

        assertThat(summary.getId()).isNull();
        assertThat(summary.getSortOrder()).isNull();
        assertThat(summary.getStorageType()).isNull();
        assertThat(summary.getObjectKey()).isNull();
        assertThat(summary.getFileExtension()).isNull();
        assertThat(summary.getSizeBytes()).isNull();
        assertThat(summary.getWidth()).isNull();
        assertThat(summary.getHeight()).isNull();
    }
}
