package jp.i432kg.footprint.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.assertInvalidValue;
import static org.assertj.core.api.Assertions.assertThat;

class ReplyCommentTest {

    @Test
    @DisplayName("ReplyComment.of は通常の本文を受け入れる")
    void should_createReplyComment_when_valueIsNormalText() {
        final ReplyComment actual = ReplyComment.of("reply");

        assertThat(actual.getValue()).isEqualTo("reply");
    }

    @Test
    @DisplayName("ReplyComment.of は改行を含む本文を受け入れる")
    void should_createReplyComment_when_valueContainsLineBreaks() {
        final ReplyComment actual = ReplyComment.of("a\nb");

        assertThat(actual.getValue()).isEqualTo("a\nb");
    }

    @Test
    @DisplayName("ReplyComment.of は null を拒否する")
    void should_throwException_when_replyCommentIsNull() {
        assertInvalidValue(() -> ReplyComment.of(null), "replyComment", "required");
    }

    @Test
    @DisplayName("ReplyComment.of は空白のみの本文を拒否する")
    void should_throwException_when_replyCommentIsBlank() {
        assertInvalidValue(() -> ReplyComment.of(" \n "), "replyComment", "blank");
    }

    @Test
    @DisplayName("ReplyComment.of は改行以外の制御文字を拒否する")
    void should_throwException_when_replyCommentContainsControlCharacters() {
        assertInvalidValue(() -> ReplyComment.of("\u0000"), "replyComment", "invalid_format");
    }

    @Test
    @DisplayName("ReplyComment.of は最大長を超える本文を拒否する")
    void should_throwException_when_replyCommentExceedsMaxLength() {
        assertInvalidValue(() -> ReplyComment.of("a".repeat(101)), "replyComment", "too_long");
    }
}
