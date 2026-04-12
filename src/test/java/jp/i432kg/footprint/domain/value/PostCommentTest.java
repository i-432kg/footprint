package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostCommentTest {

    @Test
    void of_shouldCreateInstance_whenValueIsEmpty() {
        final PostComment actual = PostComment.of("");

        assertThat(actual.getValue()).isEmpty();
    }

    @Test
    void of_shouldAllowLineBreakOnlyComment() {
        final PostComment actual = PostComment.of("\n");

        assertThat(actual.getValue()).isEqualTo("\n");
    }

    @Test
    void of_shouldRejectControlCharacters() {
        assertThatThrownBy(() -> PostComment.of("hello\u0000world"))
                .isInstanceOf(InvalidValueException.class);
    }
}
