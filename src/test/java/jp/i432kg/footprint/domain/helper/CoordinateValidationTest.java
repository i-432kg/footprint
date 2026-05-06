package jp.i432kg.footprint.domain.helper;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoordinateValidationTest {

    private static final String FIELD_NAME = "latitude";
    private static final BigDecimal MIN = new BigDecimal("-90");
    private static final BigDecimal MAX = new BigDecimal("90");
    private static final int SCALE = 6;

    @Test
    @DisplayName("CoordinateValidation.validate は範囲内の値を指定 scale で丸めて返す")
    void should_returnRoundedValue_when_valueIsWithinRange() {
        final BigDecimal actual = CoordinateValidation.validate(
                FIELD_NAME,
                new BigDecimal("35.1234567"),
                MIN,
                MAX,
                SCALE
        );

        assertThat(actual).isEqualByComparingTo("35.123457");
    }

    @Test
    @DisplayName("CoordinateValidation.validate は最小値と最大値を受け入れる")
    void should_returnValue_when_valueIsAtBoundary() {
        assertThat(CoordinateValidation.validate(FIELD_NAME, MIN, MIN, MAX, SCALE))
                .isEqualByComparingTo("-90.000000");
        assertThat(CoordinateValidation.validate(FIELD_NAME, MAX, MIN, MAX, SCALE))
                .isEqualByComparingTo("90.000000");
    }

    @Test
    @DisplayName("CoordinateValidation.validate は null を拒否する")
    void should_throwException_when_valueIsNull() {
        assertThatThrownBy(() -> CoordinateValidation.validate(FIELD_NAME, null, MIN, MAX, SCALE))
                .isInstanceOf(InvalidValueException.class)
                .satisfies(throwable -> {
                    final InvalidValueException exception = (InvalidValueException) throwable;
                    assertThat(exception.getDetails())
                            .containsEntry("target", FIELD_NAME)
                            .containsEntry("reason", "required");
                });
    }

    @Test
    @DisplayName("CoordinateValidation.validate は下限未満を拒否する")
    void should_throwException_when_valueIsLessThanMinimum() {
        assertThatThrownBy(() -> CoordinateValidation.validate(
                FIELD_NAME,
                new BigDecimal("-90.000001"),
                MIN,
                MAX,
                SCALE
        ))
                .isInstanceOf(InvalidValueException.class)
                .satisfies(throwable -> {
                    final InvalidValueException exception = (InvalidValueException) throwable;
                    assertThat(exception.getDetails())
                            .containsEntry("target", FIELD_NAME)
                            .containsEntry("reason", "out_of_range")
                            .containsEntry("rejectedValue", new BigDecimal("-90.000001"))
                            .containsEntry("min", MIN)
                            .containsEntry("max", MAX);
                });
    }

    @Test
    @DisplayName("CoordinateValidation.validate は上限超過を拒否する")
    void should_throwException_when_valueExceedsMaximum() {
        assertThatThrownBy(() -> CoordinateValidation.validate(
                FIELD_NAME,
                new BigDecimal("90.000001"),
                MIN,
                MAX,
                SCALE
        ))
                .isInstanceOf(InvalidValueException.class)
                .satisfies(throwable -> {
                    final InvalidValueException exception = (InvalidValueException) throwable;
                    assertThat(exception.getDetails())
                            .containsEntry("target", FIELD_NAME)
                            .containsEntry("reason", "out_of_range")
                            .containsEntry("rejectedValue", new BigDecimal("90.000001"))
                            .containsEntry("min", MIN)
                            .containsEntry("max", MAX);
                });
    }

    @Test
    @DisplayName("CoordinateValidation.validate は丸め後に上限超過となる値を拒否する")
    void should_throwException_when_roundedValueExceedsMaximum() {
        assertThatThrownBy(() -> CoordinateValidation.validate(
                FIELD_NAME,
                new BigDecimal("90.0000006"),
                MIN,
                MAX,
                SCALE
        ))
                .isInstanceOf(InvalidValueException.class)
                .satisfies(throwable -> {
                    final InvalidValueException exception = (InvalidValueException) throwable;
                    assertThat(exception.getDetails())
                            .containsEntry("target", FIELD_NAME)
                            .containsEntry("reason", "out_of_range")
                            .containsEntry("rejectedValue", new BigDecimal("90.000001"))
                            .containsEntry("min", MIN)
                            .containsEntry("max", MAX);
                });
    }
}
