package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileNameTest {

    @Test
    void of_shouldCreateInstance_whenValueIsValid() {
        FileName actual = FileName.of("image_01.jpg");

        assertThat(actual.value()).isEqualTo("image_01.jpg");
    }

    @Test
    void of_shouldRejectPathTraversal() {
        assertThatThrownBy(() -> FileName.of("../secret.txt"))
                .isInstanceOf(InvalidValueException.class);
    }
}
