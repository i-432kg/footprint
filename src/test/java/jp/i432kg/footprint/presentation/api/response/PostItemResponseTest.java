package jp.i432kg.footprint.presentation.api.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PostItemResponseTest {

    @Test
    @DisplayName("PostItemResponse は設定された投稿情報を保持する")
    void should_holdValues_when_allFieldsAreSet() {
        final ImageResponse firstImage = ImageResponse.of(
                "image-01",
                1,
                "https://example.com/images/post-01-1.jpg",
                "jpg",
                2048L,
                1920,
                1080
        );
        final ImageResponse secondImage = ImageResponse.of(
                "image-02",
                2,
                "https://example.com/images/post-01-2.jpg",
                "png",
                1024L,
                1280,
                720
        );
        final LocationResponse location = LocationResponse.of(35.681236, 139.767125);
        final OffsetDateTime createdAt = OffsetDateTime.of(2026, 4, 19, 10, 15, 30, 0, ZoneOffset.UTC);
        final PostItemResponse response = PostItemResponse.of(
                "post-01",
                "caption",
                List.of(firstImage, secondImage),
                true,
                location,
                createdAt
        );

        assertThat(response.getId()).isEqualTo("post-01");
        assertThat(response.getCaption()).isEqualTo("caption");
        assertThat(response.getImages()).containsExactly(firstImage, secondImage);
        assertThat(response.isHasLocation()).isTrue();
        assertThat(response.getLocation()).isEqualTo(location);
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("PostItemResponse は位置情報なし投稿として null の location を保持できる")
    void should_allowNullLocation_when_postHasNoLocation() {
        final OffsetDateTime createdAt = OffsetDateTime.of(2026, 4, 19, 10, 15, 30, 0, ZoneOffset.UTC);
        final PostItemResponse response = PostItemResponse.of(
                "post-01",
                "caption",
                List.of(),
                false,
                null,
                createdAt
        );

        assertThat(response.isHasLocation()).isFalse();
        assertThat(response.getLocation()).isNull();
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
    }

}
