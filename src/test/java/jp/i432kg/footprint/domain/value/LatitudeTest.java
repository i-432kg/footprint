package jp.i432kg.footprint.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.assertInvalidValue;
import static org.assertj.core.api.Assertions.assertThat;

class LatitudeTest {

    @Test
    @DisplayName("Latitude.of は範囲内の値を受け入れる")
    void should_createLatitude_when_valueIsWithinRange() {
        final Latitude actual = Latitude.of(new BigDecimal("35.123456"));

        assertThat(actual.getValue()).isEqualByComparingTo("35.123456");
    }

    @Test
    @DisplayName("Latitude.of は小数 6 桁へ四捨五入する")
    void should_roundLatitudeHalfUp_when_valueHasMoreThanSixDecimals() {
        assertThat(Latitude.of(new BigDecimal("35.1234564")).getValue()).isEqualByComparingTo("35.123456");
        assertThat(Latitude.of(new BigDecimal("35.1234565")).getValue()).isEqualByComparingTo("35.123457");
    }

    @Test
    @DisplayName("Latitude.of は null を拒否する")
    void should_throwException_when_latitudeIsNull() {
        assertInvalidValue(() -> Latitude.of(null), "latitude", "required");
    }

    @Test
    @DisplayName("Latitude.of は上下限値を受け入れる")
    void should_createLatitude_when_valueIsBoundary() {
        assertThat(Latitude.of(new BigDecimal("-90")).getValue()).isEqualByComparingTo("-90.000000");
        assertThat(Latitude.of(new BigDecimal("90")).getValue()).isEqualByComparingTo("90.000000");
    }

    @Test
    @DisplayName("Latitude.of は範囲外の値を拒否する")
    void should_throwException_when_latitudeIsOutOfRange() {
        assertInvalidValue(() -> Latitude.of(new BigDecimal("90.000001")), "latitude", "out_of_range");
        assertInvalidValue(() -> Latitude.of(new BigDecimal("-90.000001")), "latitude", "out_of_range");
    }
}
