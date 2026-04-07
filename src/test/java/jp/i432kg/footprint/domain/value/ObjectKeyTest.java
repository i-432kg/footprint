package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObjectKeyTest {

    @Test
    void of_shouldCreateInstance_whenValueIsValid() {
        ObjectKey actual = ObjectKey.of("users/123/posts/456/images/abc123.jpg");

        assertThat(actual.getValue()).isEqualTo("users/123/posts/456/images/abc123.jpg");
    }

    @Test
    void of_shouldRejectPathTraversal() {
        assertThatThrownBy(() -> ObjectKey.of("users/123/../secret.txt"))
                .isInstanceOf(InvalidValueException.class);
    }
}
