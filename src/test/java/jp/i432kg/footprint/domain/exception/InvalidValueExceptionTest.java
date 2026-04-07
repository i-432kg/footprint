package jp.i432kg.footprint.domain.exception;

import jp.i432kg.footprint.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidValueExceptionTest {

    @Test
    void required_shouldCreateExceptionWithExpectedDetails() {
        InvalidValueException actual = InvalidValueException.required("email");

        assertThat(actual.getErrorCode()).isEqualTo(ErrorCode.DOMAIN_INVALID_VALUE);
        assertThat(actual.getMessage()).isEqualTo("email is invalid. reason=required");
        assertThat(actual.getDetails())
                .containsEntry("target", "email")
                .containsEntry("reason", "required");
    }

    @Test
    void blank_shouldCreateExceptionWithExpectedDetails() {
        InvalidValueException actual = InvalidValueException.blank("username");

        assertThat(actual.getMessage()).isEqualTo("username is invalid. reason=blank");
        assertThat(actual.getDetails())
                .containsEntry("target", "username")
                .containsEntry("reason", "blank")
                .containsEntry("rejectedValue", "");
    }

    @Test
    void tooLong_shouldCreateExceptionWithExpectedDetails() {
        InvalidValueException actual = InvalidValueException.tooLong("comment", "abcdef", 5);

        assertThat(actual.getMessage()).isEqualTo("comment length must be less than or equal to 5.");
        assertThat(actual.getDetails())
                .containsEntry("target", "comment")
                .containsEntry("reason", "too_long")
                .containsEntry("rejectedValue", "abcdef")
                .containsEntry("maxLength", 5);
    }

    @Test
    void tooShort_shouldCreateExceptionWithExpectedDetails() {
        InvalidValueException actual = InvalidValueException.tooShort("password", "abc", 8);

        assertThat(actual.getMessage()).isEqualTo("password length must be greater than or equal to 8.");
        assertThat(actual.getDetails())
                .containsEntry("target", "password")
                .containsEntry("reason", "too_short")
                .containsEntry("rejectedValue", "abc")
                .containsEntry("minLength", 8);
    }

    @Test
    void outOfRange_shouldCreateExceptionWithExpectedDetails() {
        InvalidValueException actual = InvalidValueException.outOfRange("pixel", 9000, 320, 8192);

        assertThat(actual.getMessage()).isEqualTo("pixel must be between 320 and 8192.");
        assertThat(actual.getDetails())
                .containsEntry("target", "pixel")
                .containsEntry("reason", "out_of_range")
                .containsEntry("rejectedValue", 9000)
                .containsEntry("min", 320)
                .containsEntry("max", 8192);
    }

    @Test
    void invalidFormat_shouldCreateExceptionWithExpectedDetails() {
        InvalidValueException actual = InvalidValueException.invalidFormat("email", "invalid", "@");

        assertThat(actual.getMessage()).isEqualTo("email format is invalid.");
        assertThat(actual.getDetails())
                .containsEntry("target", "email")
                .containsEntry("reason", "invalid_format")
                .containsEntry("rejectedValue", "invalid")
                .containsEntry("expectedFormat", "@");
    }

    @Test
    void invalid_shouldCreateExceptionWithExpectedDetails() {
        InvalidValueException actual = InvalidValueException.invalid("object_key", "../secret", "cannot contain \"..\"");

        assertThat(actual.getMessage()).isEqualTo("object_key is invalid. reason=cannot contain \"..\"");
        assertThat(actual.getDetails())
                .containsEntry("target", "object_key")
                .containsEntry("reason", "cannot contain \"..\"")
                .containsEntry("rejectedValue", "../secret");
    }
}
