package jp.i432kg.footprint.presentation.api.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImageResponseTest {

    @Test
    @DisplayName("ImageResponse は設定された画像情報を保持する")
    void should_holdValues_when_allFieldsAreSet() {
        final ImageResponse response = ImageResponse.of(
                "image-01",
                1,
                "https://example.com/images/post-01.jpg",
                "jpg",
                2048L,
                1920,
                1080
        );

        assertThat(response.getId()).isEqualTo("image-01");
        assertThat(response.getSortOrder()).isEqualTo(1);
        assertThat(response.getUrl()).isEqualTo("https://example.com/images/post-01.jpg");
        assertThat(response.getFileExtension()).isEqualTo("jpg");
        assertThat(response.getSizeBytes()).isEqualTo(2048L);
        assertThat(response.getWidth()).isEqualTo(1920);
        assertThat(response.getHeight()).isEqualTo(1080);
    }

}
