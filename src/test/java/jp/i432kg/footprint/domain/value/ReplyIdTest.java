package jp.i432kg.footprint.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.VALID_ULID;
import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.assertInvalidValue;
import static org.assertj.core.api.Assertions.assertThat;

class ReplyIdTest {

    @Test
    @DisplayName("ReplyId.of は妥当な ULID を受け入れる")
    void should_createReplyId_when_valueIsValidUlid() {
        final ReplyId actual = ReplyId.of(VALID_ULID);

        assertThat(actual.getValue()).isEqualTo(VALID_ULID);
    }

    @Test
    @DisplayName("ReplyId.of は null または空白のみを拒否する")
    void should_throwException_when_replyIdIsNullOrBlank() {
        assertInvalidValue(() -> ReplyId.of(null), "reply_id", "required");
        assertInvalidValue(() -> ReplyId.of(" "), "reply_id", "blank");
    }

    @Test
    @DisplayName("ReplyId.of は ULID 形式でない値を拒否する")
    void should_throwException_when_replyIdFormatIsInvalid() {
        assertInvalidValue(() -> ReplyId.of("01ARZ3NDEKTSV4RRFFQ69G5FA"), "reply_id", "invalid_format");
        assertInvalidValue(() -> ReplyId.of("01ARZ3NDEKTSV4RRFFQ69G5FAVX"), "reply_id", "invalid_format");
        assertInvalidValue(() -> ReplyId.of("01arz3ndektsv4rrffq69g5fav"), "reply_id", "invalid_format");
        assertInvalidValue(() -> ReplyId.of("01ARZ3NDEKTSV4RRFFQ69G5FIO"), "reply_id", "invalid_format");
    }
}
