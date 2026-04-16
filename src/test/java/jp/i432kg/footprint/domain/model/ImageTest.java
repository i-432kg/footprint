package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.exception.InvalidModelException;
import jp.i432kg.footprint.domain.value.ObjectKey;
import jp.i432kg.footprint.domain.value.Pixel;
import jp.i432kg.footprint.domain.value.StorageObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImageTest {

    @Test
    @DisplayName("Image.of は妥当な画像情報から画像を生成できる")
    void should_createImage_when_valuesAreValid() {
        final LocalDateTime takenAt = LocalDateTime.of(2026, 4, 1, 12, 30);

        final Image actual = Image.of(
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
    @DisplayName("Image.hasLocation は位置情報が既知の場合に true を返す")
    void should_returnTrue_when_imageHasKnownLocation() {
        final Image actual = DomainTestFixtures.image();

        assertThat(actual.hasLocation()).isTrue();
    }

    @Test
    @DisplayName("Image.hasLocation は位置情報が不明の場合に false を返す")
    void should_returnFalse_when_imageHasUnknownLocation() {
        final Image actual = Image.of(
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
    @DisplayName("Image.of は総ピクセル数が 40MP ちょうどの場合に生成できる")
    void should_createImage_when_totalPixelsIsAtLimit() {
        final Image actual = Image.of(
                DomainTestFixtures.storageObject(),
                DomainTestFixtures.fileExtension(),
                DomainTestFixtures.fileSize(),
                Pixel.of(6250),
                Pixel.of(6400),
                DomainTestFixtures.location(),
                true,
                LocalDateTime.of(2026, 4, 1, 12, 30)
        );

        assertThat(actual.getWidth().getValue()).isEqualTo(6250);
        assertThat(actual.getHeight().getValue()).isEqualTo(6400);
    }

    @Test
    @DisplayName("Image.of は総ピクセル数が 40MP を超える場合に例外を送出する")
    void should_throwException_when_totalPixelsExceedLimit() {
        assertThatThrownBy(() -> Image.of(
                DomainTestFixtures.storageObject(),
                DomainTestFixtures.fileExtension(),
                DomainTestFixtures.fileSize(),
                Pixel.of(6251),
                Pixel.of(6400),
                DomainTestFixtures.location(),
                true,
                LocalDateTime.of(2026, 4, 1, 12, 30)
        )).isInstanceOf(InvalidModelException.class)
                .hasMessageContaining("reason=total_pixels_exceed_limit");
    }

    @Test
    @DisplayName("Image.of は短辺が 320px 未満の場合に例外を送出する")
    void should_throwException_when_shortSideIsLessThanMinimum() throws Exception {
        final Constructor<Pixel> constructor = Pixel.class.getDeclaredConstructor(int.class);
        constructor.setAccessible(true);
        final Pixel invalidShortSide = constructor.newInstance(319);

        assertThatThrownBy(() -> Image.of(
                DomainTestFixtures.storageObject(),
                DomainTestFixtures.fileExtension(),
                DomainTestFixtures.fileSize(),
                invalidShortSide,
                DomainTestFixtures.height(),
                DomainTestFixtures.location(),
                true,
                LocalDateTime.of(2026, 4, 1, 12, 30)
        )).isInstanceOf(InvalidModelException.class)
                .hasMessageContaining("reason=short_side_pixels_too_small");
    }
}
