package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReplyCommentTest {

    @Test
    void of_shouldCreateInstance_whenValueIsValid() {
        final ReplyComment actual = ReplyComment.of("line1\nline2");

        assertThat(actual.getValue()).isEqualTo("line1\nline2");
    }

    @Test
    void of_shouldRejectBlankValue() {
        assertThatThrownBy(() -> ReplyComment.of(" \n "))
                .isInstanceOf(InvalidValueException.class);
    }

    @Test
    void of_shouldRejectControlCharacters() {
        assertThatThrownBy(() -> ReplyComment.of("hello\u0000world"))
                .isInstanceOf(InvalidValueException.class);
    }
}
