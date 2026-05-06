package jp.i432kg.footprint.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.assertInvalidValue;
import static org.assertj.core.api.Assertions.assertThat;

class PixelTest {

    @Test
    @DisplayName("Pixel.of は範囲内の値を受け入れる")
    void should_createPixel_when_valueIsWithinRange() {
        final Pixel actual = Pixel.of(1024);

        assertThat(actual.getValue()).isEqualTo(1024);
    }

    @Test
    @DisplayName("Pixel.of は下限値 0 と上限値 100000 を受け入れる")
    void should_createPixel_when_valueIsBoundary() {
        assertThat(Pixel.of(0).getValue()).isZero();
        assertThat(Pixel.of(100_000).getValue()).isEqualTo(100_000);
    }

    @Test
    @DisplayName("Pixel.of は 0 未満の値を拒否する")
    void should_throwException_when_pixelIsOutOfRange() {
        assertInvalidValue(() -> Pixel.of(-1), "pixel", "out_of_range");
    }

    @Test
    @DisplayName("Pixel.of は上限値 100000 を超える値を拒否する")
    void should_throwException_when_pixelExceedsMaximum() {
        assertInvalidValue(() -> Pixel.of(100_001), "pixel", "out_of_range");
    }
}
