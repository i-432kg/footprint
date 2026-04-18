package jp.i432kg.footprint.application.exception;

import jp.i432kg.footprint.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationExceptionTest {

    @Test
    @DisplayName("ApplicationException は基本情報を保持する")
    void should_holdBasicProperties_when_createdWithoutCause() {
        final TestApplicationException exception = new TestApplicationException(
                ErrorCode.UNEXPECTED_ERROR,
                "application error",
                Map.of("key", "value")
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNEXPECTED_ERROR);
        assertThat(exception.getMessage()).isEqualTo("application error");
        assertThat(exception.getDetails()).containsEntry("key", "value");
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("ApplicationException は cause を保持する")
    void should_holdCause_when_createdWithCause() {
        final IllegalStateException cause = new IllegalStateException("boom");
        final TestApplicationException exception = new TestApplicationException(
                ErrorCode.UNEXPECTED_ERROR,
                "application error",
                Map.of("key", "value"),
                cause
        );

        assertThat(exception.getCause()).isSameAs(cause);
    }

    private static final class TestApplicationException extends ApplicationException {
        private TestApplicationException(
                final ErrorCode errorCode,
                final String message,
                final Map<String, Object> details
        ) {
            super(errorCode, message, details);
        }

        private TestApplicationException(
                final ErrorCode errorCode,
                final String message,
                final Map<String, Object> details,
                final Throwable cause
        ) {
            super(errorCode, message, details, cause);
        }
    }
}
