package jp.i432kg.footprint.domain.exception;

import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailAlreadyUsedExceptionTest {

    @Test
    void constructor_shouldSetMessageErrorCodeAndDetails() {
        EmailAddress emailAddress = EmailAddress.of("user@example.com");

        EmailAlreadyUsedException actual = new EmailAlreadyUsedException(emailAddress);

        assertThat(actual.getMessage()).isEqualTo("this email address is already used");
        assertThat(actual.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_USED);
        assertThat(actual.getDetails()).containsEntry("emailAddress", emailAddress);
    }
}
