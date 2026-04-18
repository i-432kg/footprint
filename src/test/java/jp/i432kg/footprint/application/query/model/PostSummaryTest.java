package jp.i432kg.footprint.application.query.model;

import jp.i432kg.footprint.domain.value.ObjectKey;
import jp.i432kg.footprint.domain.value.StorageType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PostSummaryTest {

    @Test
    @DisplayName("PostSummary は設定された投稿参照情報を保持する")
    void should_holdValues_when_allFieldsAreSet() {
        final ImageSummary imageSummary = new ImageSummary(
                "image-01",
                1,
                StorageType.LOCAL,
                ObjectKey.of("users/user-01/posts/post-01/images/image-01.jpg"),
                "jpg",
                2048L,
                1920,
                1080
        );
        final LocationSummary locationSummary = new LocationSummary(35.681236, 139.767125);
        final LocalDateTime createdAt = LocalDateTime.of(2026, 4, 18, 19, 15, 30);
        final PostSummary summary = new PostSummary(
                "post-01",
                "caption",
                List.of(imageSummary),
                true,
                locationSummary,
                createdAt
        );

        assertThat(summary.getId()).isEqualTo("post-01");
        assertThat(summary.getCaption()).isEqualTo("caption");
        assertThat(summary.getImages()).containsExactly(imageSummary);
        assertThat(summary.isHasLocation()).isTrue();
        assertThat(summary.getLocation()).isEqualTo(locationSummary);
        assertThat(summary.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("PostSummary は位置情報なし投稿として null の location を保持できる")
    void should_allowNullLocation_when_postHasNoLocation() {
        final PostSummary summary = new PostSummary(
                "post-01",
                "caption",
                List.of(),
                false,
                null,
                LocalDateTime.of(2026, 4, 18, 19, 15, 30)
        );

        assertThat(summary.isHasLocation()).isFalse();
        assertThat(summary.getLocation()).isNull();
    }

    @Test
    @DisplayName("PostSummary は no-args 生成時に既定値で初期化される")
    void should_initializeDefaultValues_when_createdWithNoArgsConstructor() {
        final PostSummary summary = new PostSummary();

        assertThat(summary.getId()).isNull();
        assertThat(summary.getCaption()).isNull();
        assertThat(summary.getImages()).isNull();
        assertThat(summary.isHasLocation()).isFalse();
        assertThat(summary.getLocation()).isNull();
        assertThat(summary.getCreatedAt()).isNull();
    }
}
