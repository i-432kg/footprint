package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.value.ObjectKey;
import jp.i432kg.footprint.domain.value.Pixel;
import jp.i432kg.footprint.domain.value.StorageObject;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImageTest {

    @Test
    void of_shouldCreateInstanceWithGivenValues() {
        LocalDateTime takenAt = LocalDateTime.of(2026, 4, 1, 12, 30);

        Image actual = Image.of(
                DomainTestFixtures.storageObject(),
                DomainTestFixtures.fileExtension(),
                DomainTestFixtures.fileSize(),
                DomainTestFixtures.width(),
                DomainTestFixtures.height(),
                DomainTestFixtures.location(),
                true,
                takenAt
        );

        assertThat(actual.getStorageObject()).isEqualTo(DomainTestFixtures.storageObject());
        assertThat(actual.getFileExtension()).isEqualTo(DomainTestFixtures.fileExtension());
        assertThat(actual.getFileSize()).isEqualTo(DomainTestFixtures.fileSize());
        assertThat(actual.getWidth()).isEqualTo(DomainTestFixtures.width());
        assertThat(actual.getHeight()).isEqualTo(DomainTestFixtures.height());
        assertThat(actual.getLocation()).isEqualTo(DomainTestFixtures.location());
        assertThat(actual.isHasEXIF()).isTrue();
        assertThat(actual.getTakenAt()).isEqualTo(takenAt);
    }

    @Test
    void hasLocation_shouldReturnTrue_whenLocationIsKnown() {
        Image actual = DomainTestFixtures.image();

        assertThat(actual.hasLocation()).isTrue();
    }

    @Test
    void hasLocation_shouldReturnFalse_whenLocationIsUnknown() {
        Image actual = Image.of(
                StorageObject.local(ObjectKey.of("users/a/posts/b/images/c.jpg")),
                DomainTestFixtures.fileExtension(),
                DomainTestFixtures.fileSize(),
                DomainTestFixtures.width(),
                DomainTestFixtures.height(),
                Location.unknown(),
                false,
                LocalDateTime.of(2026, 4, 1, 10, 0)
        );

        assertThat(actual.hasLocation()).isFalse();
    }

    @Test
    void of_shouldRejectWhenTotalPixelsExceedFortyMegaPixels() {
        assertThatThrownBy(() -> Image.of(
                DomainTestFixtures.storageObject(),
                DomainTestFixtures.fileExtension(),
                DomainTestFixtures.fileSize(),
                Pixel.of(5001),
                Pixel.of(8000),
                DomainTestFixtures.location(),
                true,
                LocalDateTime.of(2026, 4, 1, 12, 30)
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Total pixels exceed the limit of 40MP");
    }

}
