package jp.i432kg.footprint.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.assertInvalidValue;
import static org.assertj.core.api.Assertions.assertThat;

class LongitudeTest {

    @Test
    @DisplayName("Longitude.of は範囲内の値を受け入れる")
    void should_createLongitude_when_valueIsWithinRange() {
        final Longitude actual = Longitude.of(new BigDecimal("139.123456"));

        assertThat(actual.getValue()).isEqualByComparingTo("139.123456");
    }

    @Test
    @DisplayName("Longitude.of は小数 6 桁へ四捨五入する")
    void should_roundLongitudeHalfUp_when_valueHasMoreThanSixDecimals() {
        assertThat(Longitude.of(new BigDecimal("139.1234564")).getValue()).isEqualByComparingTo("139.123456");
        assertThat(Longitude.of(new BigDecimal("139.1234565")).getValue()).isEqualByComparingTo("139.123457");
    }

    @Test
    @DisplayName("Longitude.of は null を拒否する")
    void should_throwException_when_longitudeIsNull() {
        assertInvalidValue(() -> Longitude.of(null), "longitude", "required");
    }

    @Test
    @DisplayName("Longitude.of は上下限値を受け入れる")
    void should_createLongitude_when_valueIsBoundary() {
        assertThat(Longitude.of(new BigDecimal("-180")).getValue()).isEqualByComparingTo("-180.000000");
        assertThat(Longitude.of(new BigDecimal("180")).getValue()).isEqualByComparingTo("180.000000");
    }

    @Test
    @DisplayName("Longitude.of は範囲外の値を拒否する")
    void should_throwException_when_longitudeIsOutOfRange() {
        assertInvalidValue(() -> Longitude.of(new BigDecimal("180.000001")), "longitude", "out_of_range");
        assertInvalidValue(() -> Longitude.of(new BigDecimal("-180.000001")), "longitude", "out_of_range");
    }
}
