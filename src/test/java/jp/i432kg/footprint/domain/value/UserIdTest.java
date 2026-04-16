package jp.i432kg.footprint.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.VALID_ULID;
import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.assertInvalidValue;
import static org.assertj.core.api.Assertions.assertThat;

class UserIdTest {

    @Test
    @DisplayName("UserId.of は妥当な ULID を受け入れる")
    void should_createUserId_when_valueIsValidUlid() {
        final UserId actual = UserId.of(VALID_ULID);

        assertThat(actual.getValue()).isEqualTo(VALID_ULID);
    }

    @Test
    @DisplayName("UserId.of は null または空白のみを拒否する")
    void should_throwException_when_userIdIsNullOrBlank() {
        assertInvalidValue(() -> UserId.of(null), "user_id", "required");
        assertInvalidValue(() -> UserId.of(" "), "user_id", "blank");
    }

    @Test
    @DisplayName("UserId.of は ULID 形式でない値を拒否する")
    void should_throwException_when_userIdFormatIsInvalid() {
        assertInvalidValue(() -> UserId.of("01ARZ3NDEKTSV4RRFFQ69G5FA"), "user_id", "invalid_format");
        assertInvalidValue(() -> UserId.of("01ARZ3NDEKTSV4RRFFQ69G5FAVX"), "user_id", "invalid_format");
        assertInvalidValue(() -> UserId.of("01arz3ndektsv4rrffq69g5fav"), "user_id", "invalid_format");
        assertInvalidValue(() -> UserId.of("01ARZ3NDEKTSV4RRFFQ69G5FIO"), "user_id", "invalid_format");
    }
}
