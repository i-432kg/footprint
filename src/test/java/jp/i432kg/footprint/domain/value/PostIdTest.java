package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostIdTest {

    @Test
    void of_shouldCreateInstance_whenValueIsValidUlid() {
        PostId actual = PostId.of(DomainTestFixtures.POST_ID);

        assertThat(actual.value()).isEqualTo(DomainTestFixtures.POST_ID);
    }

    @Test
    void of_shouldRejectInvalidFormat() {
        assertThatThrownBy(() -> PostId.of("invalid"))
                .isInstanceOf(InvalidValueException.class);
    }
}
