package jp.i432kg.footprint.application.exception.usecase;

import jp.i432kg.footprint.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserCommandFailedExceptionTest {

    @Test
    @DisplayName("UserCommandFailedException.saveFailed は保存失敗情報を組み立てる")
    void should_buildSaveFailedDetails_when_factoryIsUsed() {
        final IllegalStateException cause = new IllegalStateException("db down");
        final UserCommandFailedException exception = UserCommandFailedException.saveFailed("01ARZ3NDEKTSV4RRFFQ69G5FAV", cause);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_COMMAND_FAILED);
        assertThat(exception.getMessage()).isEqualTo("user is invalid. reason=save_failed");
        assertThat(exception.getDetails())
                .containsEntry("target", "user")
                .containsEntry("reason", "save_failed")
                .containsEntry("rejectedValue", "01ARZ3NDEKTSV4RRFFQ69G5FAV");
        assertThat(exception.getCause()).isSameAs(cause);
    }
}
