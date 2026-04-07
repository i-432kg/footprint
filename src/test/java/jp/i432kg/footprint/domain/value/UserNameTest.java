package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserNameTest {

    @Test
    void of_shouldTrimAndStoreValue() {
        UserName actual = UserName.of("  user_01  ");

        assertThat(actual.value()).isEqualTo("user_01");
    }

    @Test
    void of_shouldRejectWhitespaceInsideValue() {
        assertThatThrownBy(() -> UserName.of("user name"))
                .isInstanceOf(InvalidValueException.class);
    }
}
