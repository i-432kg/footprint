package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ByteTest {

    @Test
    void of_shouldCreateInstance_whenValueIsWithinRange() {
        jp.i432kg.footprint.domain.value.Byte actual = jp.i432kg.footprint.domain.value.Byte.of(10_485_760L);

        assertThat(actual.value()).isEqualTo(10_485_760L);
    }

    @Test
    void of_shouldRejectValueOutsideRange() {
        assertThatThrownBy(() -> jp.i432kg.footprint.domain.value.Byte.of(-1L))
                .isInstanceOf(InvalidValueException.class);
    }
}
