package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LongitudeTest {

    @Test
    void of_shouldRoundToSixDecimalPlaces() {
        Longitude actual = Longitude.of(new BigDecimal("139.7671254"));

        assertThat(actual.value()).isEqualByComparingTo(new BigDecimal("139.767125"));
    }

    @Test
    void of_shouldRejectValueOutsideRange() {
        assertThatThrownBy(() -> Longitude.of(new BigDecimal("180.000001")))
                .isInstanceOf(InvalidValueException.class);
    }
}
