package jp.i432kg.footprint.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.assertInvalidValue;
import static org.assertj.core.api.Assertions.assertThat;

class ByteTest {

    @Test
    @DisplayName("Byte.of は範囲内の値を生成できる")
    void should_createByte_when_valueIsWithinRange() {
        final Byte actual = Byte.of(1024);

        assertThat(actual.getValue()).isEqualTo(1024);
    }

    @Test
    @DisplayName("Byte.of は下限値 0 を受け入れる")
    void should_createByte_when_valueIsMinBoundary() {
        final Byte actual = Byte.of(0);

        assertThat(actual.getValue()).isZero();
    }

    @Test
    @DisplayName("Byte.of は上限値 10MB を受け入れる")
    void should_createByte_when_valueIsMaxBoundary() {
        final Byte actual = Byte.of(10_485_760);

        assertThat(actual.getValue()).isEqualTo(10_485_760);
    }

    @Test
    @DisplayName("Byte.of は負の値を拒否する")
    void should_throwException_when_valueIsBelowMin() {
        assertInvalidValue(() -> Byte.of(-1), "byte", "out_of_range");
    }

    @Test
    @DisplayName("Byte.of は上限超過を拒否する")
    void should_throwException_when_valueExceedsMax() {
        assertInvalidValue(() -> Byte.of(10_485_761), "byte", "out_of_range");
    }
}
