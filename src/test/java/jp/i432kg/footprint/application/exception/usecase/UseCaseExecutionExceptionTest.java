package jp.i432kg.footprint.application.exception.usecase;

import jp.i432kg.footprint.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UseCaseExecutionExceptionTest {

    @Test
    @DisplayName("UseCaseExecutionException は実行失敗情報を保持する")
    void should_holdBasicProperties_when_createdWithoutCause() {
        final TestUseCaseExecutionException exception = new TestUseCaseExecutionException(
                ErrorCode.POST_COMMAND_FAILED,
                "use case failed",
                Map.of("target", "post")
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POST_COMMAND_FAILED);
        assertThat(exception.getMessage()).isEqualTo("use case failed");
        assertThat(exception.getDetails()).containsEntry("target", "post");
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("UseCaseExecutionException は cause を保持する")
    void should_holdCause_when_createdWithCause() {
        final IllegalStateException cause = new IllegalStateException("boom");
        final TestUseCaseExecutionException exception = new TestUseCaseExecutionException(
                ErrorCode.POST_COMMAND_FAILED,
                "use case failed",
                Map.of("target", "post"),
                cause
        );

        assertThat(exception.getCause()).isSameAs(cause);
    }

    private static final class TestUseCaseExecutionException extends UseCaseExecutionException {
        private TestUseCaseExecutionException(
                final ErrorCode errorCode,
                final String message,
                final Map<String, Object> details
        ) {
            super(errorCode, message, details);
        }

        private TestUseCaseExecutionException(
                final ErrorCode errorCode,
                final String message,
                final Map<String, Object> details,
                final Throwable cause
        ) {
            super(errorCode, message, details, cause);
        }
    }
}
