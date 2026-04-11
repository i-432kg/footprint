package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImageIdTest {

    @Test
    void of_shouldCreateInstance_whenValueIsValidUlid() {
        ImageId actual = ImageId.of(DomainTestFixtures.IMAGE_ID);

        assertThat(actual.getValue()).isEqualTo(DomainTestFixtures.IMAGE_ID);
    }

    @Test
    void of_shouldRejectInvalidFormat() {
        assertThatThrownBy(() -> ImageId.of("invalid"))
                .isInstanceOf(InvalidValueException.class);
    }

    @Test
    void of_shouldRejectValueWithLeadingOrTrailingWhitespace() {
        assertThatThrownBy(() -> ImageId.of(" " + DomainTestFixtures.IMAGE_ID + " "))
                .isInstanceOf(InvalidValueException.class);
    }
}
