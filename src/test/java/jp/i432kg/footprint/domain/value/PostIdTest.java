package jp.i432kg.footprint.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.VALID_ULID;
import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.assertInvalidValue;
import static org.assertj.core.api.Assertions.assertThat;

class PostIdTest {

    @Test
    @DisplayName("PostId.of は妥当な ULID を受け入れる")
    void should_createPostId_when_valueIsValidUlid() {
        final PostId actual = PostId.of(VALID_ULID);

        assertThat(actual.getValue()).isEqualTo(VALID_ULID);
    }

    @Test
    @DisplayName("PostId.of は null または空白のみを拒否する")
    void should_throwException_when_postIdIsNullOrBlank() {
        assertInvalidValue(() -> PostId.of(null), "post_id", "required");
        assertInvalidValue(() -> PostId.of(" "), "post_id", "blank");
    }

    @Test
    @DisplayName("PostId.of は ULID 形式でない値を拒否する")
    void should_throwException_when_postIdFormatIsInvalid() {
        assertInvalidValue(() -> PostId.of("01ARZ3NDEKTSV4RRFFQ69G5FA"), "post_id", "invalid_format");
        assertInvalidValue(() -> PostId.of("01ARZ3NDEKTSV4RRFFQ69G5FAVX"), "post_id", "invalid_format");
        assertInvalidValue(() -> PostId.of("01arz3ndektsv4rrffq69g5fav"), "post_id", "invalid_format");
        assertInvalidValue(() -> PostId.of("01ARZ3NDEKTSV4RRFFQ69G5FIO"), "post_id", "invalid_format");
    }
}
