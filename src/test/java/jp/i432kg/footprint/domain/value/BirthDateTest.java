package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BirthDateTest {

    @Test
    void of_shouldCreateInstance_whenValueIsTodayOrPast() {
        BirthDate actual = BirthDate.of(LocalDate.now());

        assertThat(actual.value()).isEqualTo(LocalDate.now());
    }

    @Test
    void of_shouldRejectFutureDate() {
        assertThatThrownBy(() -> BirthDate.of(LocalDate.now().plusDays(1)))
                .isInstanceOf(InvalidValueException.class);
    }
}
