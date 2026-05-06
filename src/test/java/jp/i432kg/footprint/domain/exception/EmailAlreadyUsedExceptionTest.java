package jp.i432kg.footprint.domain.exception;

import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailAlreadyUsedExceptionTest {

    @Test
    @DisplayName("EmailAlreadyUsedException は message と errorCode と details を保持する")
    void should_setMessageErrorCodeAndDetails_when_constructed() {
        final EmailAddress emailAddress = EmailAddress.of("user@example.com");

        final EmailAlreadyUsedException actual = new EmailAlreadyUsedException(emailAddress);

        assertThat(actual.getMessage()).isEqualTo("this email address is already used");
        assertThat(actual.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_USED);
        assertThat(actual.getDetails())
                .containsEntry("target", "email")
                .containsEntry("reason", "already_used")
                .containsEntry("rejectedValue", emailAddress);
    }
}
