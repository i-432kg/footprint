package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailAddressTest {

    @Test
    void of_shouldNormalizeToLowerCaseAndTrim() {
        EmailAddress actual = EmailAddress.of("  USER.Name+tag@Example.COM  ");

        assertThat(actual.value()).isEqualTo("user.name+tag@example.com");
    }

    @Test
    void of_shouldRejectInvalidFormat() {
        assertThatThrownBy(() -> EmailAddress.of("invalid-email"))
                .isInstanceOf(InvalidValueException.class);
    }
}
