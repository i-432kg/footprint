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

        final BirthDate actual = BirthDate.of(today, today);

        assertThat(actual.getValue()).isEqualTo(today);
    }

    @Test
    @DisplayName("BirthDate.of は過去日の生年月日を生成できる")
    void should_createBirthDate_when_valueIsPastDate() {
        final LocalDate today = LocalDate.now();
        final LocalDate yesterday = today.minusDays(1);

        final BirthDate actual = BirthDate.of(yesterday, today);

        assertThat(actual.getValue()).isEqualTo(yesterday);
    }

    @Test
    @DisplayName("BirthDate.of は null を拒否する")
    void should_throwException_when_birthDateIsNull() {
        assertInvalidValue(() -> BirthDate.of(null, LocalDate.now()), "birthdate", "required");
    }

    @Test
    @DisplayName("BirthDate.of は未来日を拒否する")
    void should_throwException_when_birthDateIsFuture() {
        final LocalDate today = LocalDate.now();
        final LocalDate tomorrow = today.plusDays(1);

        assertInvalidValue(
                () -> BirthDate.of(tomorrow, today),
                "birthdate",
                "must not be in the future"
        );
    }

    @Test
    @DisplayName("BirthDate.restore は未来日の生年月日を復元できる")
    void should_restoreBirthDate_when_valueIsFutureDate() {
        final LocalDate futureDate = LocalDate.now().plusDays(1);

        final BirthDate actual = BirthDate.restore(futureDate);

        assertThat(actual.getValue()).isEqualTo(futureDate);
    }

    @Test
    @DisplayName("BirthDate.restore は null を拒否する")
    void should_throwException_when_restoredBirthDateIsNull() {
        assertInvalidValue(() -> BirthDate.restore(null), "birthdate", "required");
    }
}
