package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RawPasswordTest {

    @Test
    void of_shouldCreateInstance_whenValueIsValid() {
        RawPassword actual = RawPassword.of("Passw0rd!");

        assertThat(actual.getValue()).isEqualTo("Passw0rd!");
    }

    @Test
    void toString_shouldMaskSensitiveValue() {
        assertThat(RawPassword.of("Passw0rd!").toString())
                .contains("********")
                .doesNotContain("Passw0rd!");
    }

    @Test
    void of_shouldRejectWhitespaceInsideValue() {
        assertThatThrownBy(() -> RawPassword.of("Pass word"))
                .isInstanceOf(InvalidValueException.class);
    }
}
