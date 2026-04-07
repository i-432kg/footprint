package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserIdTest {

    @Test
    void of_shouldCreateInstance_whenValueIsValidUlid() {
        UserId actual = UserId.of(DomainTestFixtures.USER_ID);

        assertThat(actual.value()).isEqualTo(DomainTestFixtures.USER_ID);
    }

    @Test
    void of_shouldRejectInvalidFormat() {
        assertThatThrownBy(() -> UserId.of("invalid"))
                .isInstanceOf(InvalidValueException.class);
    }

    @Test
    void of_shouldRejectValueWithLeadingOrTrailingWhitespace() {
        assertThatThrownBy(() -> UserId.of(" " + DomainTestFixtures.USER_ID + " "))
                .isInstanceOf(InvalidValueException.class);
    }
}
