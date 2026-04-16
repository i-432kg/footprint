package jp.i432kg.footprint.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.assertInvalidValue;
import static org.assertj.core.api.Assertions.assertThat;

class PostCommentTest {

    @Test
    @DisplayName("PostComment.of は通常の本文を受け入れる")
    void should_createPostComment_when_valueIsNormalText() {
        final PostComment actual = PostComment.of("comment");

        assertThat(actual.getValue()).isEqualTo("comment");
    }

    @Test
    @DisplayName("PostComment.of は空文字を受け入れる")
    void should_createPostComment_when_valueIsEmptyString() {
        final PostComment actual = PostComment.of("");

        assertThat(actual.getValue()).isEmpty();
    }

    @Test
    @DisplayName("PostComment.of は改行を含む本文を受け入れる")
    void should_createPostComment_when_valueContainsLineBreaks() {
        final PostComment actual = PostComment.of("a\nb\r\nc");

        assertThat(actual.getValue()).isEqualTo("a\nb\r\nc");
    }

    @Test
    @DisplayName("PostComment.of は null を拒否する")
    void should_throwException_when_postCommentIsNull() {
        assertInvalidValue(() -> PostComment.of(null), "postComment", "required");
    }

    @Test
    @DisplayName("PostComment.of は改行以外の制御文字を拒否する")
    void should_throwException_when_postCommentContainsControlCharacters() {
        assertInvalidValue(() -> PostComment.of("\u0000"), "postComment", "invalid_format");
    }

    @Test
    @DisplayName("PostComment.of は最大長を超える本文を拒否する")
    void should_throwException_when_postCommentExceedsMaxLength() {
        assertInvalidValue(() -> PostComment.of("a".repeat(101)), "postComment", "too_long");
    }
}
