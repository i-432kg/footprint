package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReplyIdTest {

    @Test
    void of_shouldCreateInstance_whenValueIsValidUlid() {
        ReplyId actual = ReplyId.of(DomainTestFixtures.REPLY_ID);

        assertThat(actual.value()).isEqualTo(DomainTestFixtures.REPLY_ID);
    }

    @Test
    void of_shouldRejectInvalidFormat() {
        assertThatThrownBy(() -> ReplyId.of("invalid"))
                .isInstanceOf(InvalidValueException.class);
    }

    @Test
    void of_shouldRejectValueWithLeadingOrTrailingWhitespace() {
        assertThatThrownBy(() -> ReplyId.of(" " + DomainTestFixtures.REPLY_ID + " "))
                .isInstanceOf(InvalidValueException.class);
    }
}
