package jp.i432kg.footprint.application.exception.resource;

import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserNotFoundExceptionTest {

    @Test
    @DisplayName("UserNotFoundException はユーザー not found 情報を組み立てる")
    void should_buildUserNotFoundDetails_when_created() {
        final UserNotFoundException exception =
                new UserNotFoundException(UserId.of("01ARZ3NDEKTSV4RRFFQ69G5FAV"));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo("User not found. userId=01ARZ3NDEKTSV4RRFFQ69G5FAV");
        assertThat(exception.getDetails()).containsEntry("userId", "01ARZ3NDEKTSV4RRFFQ69G5FAV");
    }
}
