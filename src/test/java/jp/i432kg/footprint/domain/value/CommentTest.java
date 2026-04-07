package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommentTest {

    @Test
    void of_shouldCreateInstance_whenValueIsValid() {
        Comment actual = Comment.of("line1\nline2");

        assertThat(actual.value()).isEqualTo("line1\nline2");
    }

    @Test
    void of_shouldRejectControlCharacters() {
        assertThatThrownBy(() -> Comment.of("hello\u0000world"))
                .isInstanceOf(InvalidValueException.class);
    }
}
