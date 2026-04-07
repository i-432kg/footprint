package jp.i432kg.footprint.domain.exception;

import jp.i432kg.footprint.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DomainExceptionTest {

    @Test
    void constructor_shouldSetMessageErrorCodeAndDetails() {
        TestDomainException actual = new TestDomainException(
                ErrorCode.DOMAIN_INVALID_VALUE,
                "test message",
                Map.of("key", "value")
        );

        assertThat(actual.getMessage()).isEqualTo("test message");
        assertThat(actual.getErrorCode()).isEqualTo(ErrorCode.DOMAIN_INVALID_VALUE);
        assertThat(actual.getDetails()).containsEntry("key", "value");
    }

    @Test
    void constructorWithCause_shouldSetCause() {
        RuntimeException cause = new RuntimeException("boom");

        TestDomainException actual = new TestDomainException(
                ErrorCode.UNEXPECTED_ERROR,
                "test message",
                Map.of("key", "value"),
                cause
        );

        assertThat(actual.getCause()).isSameAs(cause);
        assertThat(actual.getErrorCode()).isEqualTo(ErrorCode.UNEXPECTED_ERROR);
    }

    private static final class TestDomainException extends DomainException {
        private TestDomainException(ErrorCode errorCode, String message, Map<String, Object> details) {
            super(errorCode, message, details);
        }

        private TestDomainException(ErrorCode errorCode, String message, Map<String, Object> details, Throwable cause) {
            super(errorCode, message, details, cause);
        }
    }
}
