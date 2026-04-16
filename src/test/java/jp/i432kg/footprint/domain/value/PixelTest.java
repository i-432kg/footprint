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
    @DisplayName("Pixel.of は上下限値を受け入れる")
    void should_createPixel_when_valueIsBoundary() {
        assertThat(Pixel.of(320).getValue()).isEqualTo(320);
        assertThat(Pixel.of(8192).getValue()).isEqualTo(8192);
    }

    @Test
    @DisplayName("Pixel.of は範囲外の値を拒否する")
    void should_throwException_when_pixelIsOutOfRange() {
        assertInvalidValue(() -> Pixel.of(319), "pixel", "out_of_range");
        assertInvalidValue(() -> Pixel.of(8193), "pixel", "out_of_range");
    }
}
