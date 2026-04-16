package jp.i432kg.footprint.domain.exception;

import jp.i432kg.footprint.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DomainExceptionTest {

    @Test
    @DisplayName("DomainException は message と errorCode と details を保持する")
    void should_setMessageErrorCodeAndDetails_when_constructed() {
        final TestDomainException actual = new TestDomainException(
                ErrorCode.DOMAIN_INVALID_VALUE,
                "test message",
                Map.of("key", "value")
        );

        assertThat(actual.getMessage()).isEqualTo("test message");
        assertThat(actual.getErrorCode()).isEqualTo(ErrorCode.DOMAIN_INVALID_VALUE);
        assertThat(actual.getDetails()).containsEntry("key", "value");
    }

    @Test
    @DisplayName("DomainException は cause 付きコンストラクタで cause を保持する")
    void should_setCause_when_constructedWithCause() {
        final RuntimeException cause = new RuntimeException("boom");

        final TestDomainException actual = new TestDomainException(
                ErrorCode.UNEXPECTED_ERROR,
                "test message",
                Map.of("key", "value"),
                cause
        );

        assertThat(actual.getCause()).isSameAs(cause);
        assertThat(actual.getErrorCode()).isEqualTo(ErrorCode.UNEXPECTED_ERROR);
        assertThat(actual.getDetails()).containsEntry("key", "value");
    }

    private static final class TestDomainException extends DomainException {

        private TestDomainException(
                final ErrorCode errorCode,
                final String message,
                final Map<String, Object> details
        ) {
            super(errorCode, message, details);
        }

        private TestDomainException(
                final ErrorCode errorCode,
                final String message,
                final Map<String, Object> details,
                final Throwable cause
        ) {
            super(errorCode, message, details, cause);
        }
    }
}
