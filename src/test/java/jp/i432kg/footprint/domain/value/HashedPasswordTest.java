package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HashedPasswordTest {

    @Test
    void from_shouldHashRawPasswordUsingHasher() {
        HashedPassword actual = HashedPassword.from(
                RawPassword.of("Passw0rd!"),
                rawPassword -> "hashed-" + rawPassword
        );

        assertThat(actual.value()).isEqualTo("hashed-Passw0rd!");
    }

    @Test
    void of_shouldRejectBlankValue() {
        assertThatThrownBy(() -> HashedPassword.of(" "))
                .isInstanceOf(InvalidValueException.class);
    }
}
