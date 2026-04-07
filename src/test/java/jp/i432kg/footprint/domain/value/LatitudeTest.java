package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LatitudeTest {

    @Test
    void of_shouldRoundToSixDecimalPlaces() {
        Latitude actual = Latitude.of(new BigDecimal("35.1234567"));

        assertThat(actual.value()).isEqualByComparingTo(new BigDecimal("35.123457"));
    }

    @Test
    void of_shouldRejectValueOutsideRange() {
        assertThatThrownBy(() -> Latitude.of(new BigDecimal("90.000001")))
                .isInstanceOf(InvalidValueException.class);
    }
}
