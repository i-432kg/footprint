package jp.i432kg.footprint.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.assertInvalidValue;
import static org.assertj.core.api.Assertions.assertThat;

class BirthDateTest {

    @Test
    @DisplayName("BirthDate.of は当日の生年月日を生成できる")
    void should_createBirthDate_when_valueIsToday() {
        final LocalDate today = LocalDate.now();

        final BirthDate actual = BirthDate.of(today);

        assertThat(actual.getValue()).isEqualTo(today);
    }

    @Test
    @DisplayName("BirthDate.of は過去日の生年月日を生成できる")
    void should_createBirthDate_when_valueIsPastDate() {
        final LocalDate yesterday = LocalDate.now().minusDays(1);

        final BirthDate actual = BirthDate.of(yesterday);

        assertThat(actual.getValue()).isEqualTo(yesterday);
    }

    @Test
    @DisplayName("BirthDate.of は null を拒否する")
    void should_throwException_when_birthDateIsNull() {
        assertInvalidValue(() -> BirthDate.of(null), "birthdate", "required");
    }

    @Test
    @DisplayName("BirthDate.of は未来日を拒否する")
    void should_throwException_when_birthDateIsFuture() {
        final LocalDate tomorrow = LocalDate.now().plusDays(1);

        assertInvalidValue(
                () -> BirthDate.of(tomorrow),
                "birthdate",
                "must not be in the future"
        );
    }
}
