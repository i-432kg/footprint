package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PixelTest {

    @Test
    void of_shouldCreateInstance_whenValueIsWithinRange() {
        Pixel actual = Pixel.of(320);

        assertThat(actual.value()).isEqualTo(320);
    }

    @Test
    void of_shouldRejectValueOutsideRange() {
        assertThatThrownBy(() -> Pixel.of(9000))
                .isInstanceOf(InvalidValueException.class);
    }
}
