package jp.i432kg.footprint.domain.exception;

import jp.i432kg.footprint.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidValueExceptionTest {

    @Test
    @DisplayName("InvalidValueException.required は required の details を持つ例外を生成する")
    void should_createRequiredException_withExpectedDetails() {
        final InvalidValueException actual = InvalidValueException.required("email");

        assertThat(actual.getErrorCode()).isEqualTo(ErrorCode.DOMAIN_INVALID_VALUE);
        assertThat(actual.getMessage()).isEqualTo("email is invalid. reason=required");
        assertThat(actual.getDetails())
                .containsEntry("target", "email")
                .containsEntry("reason", "required");
    }

    @Test
    @DisplayName("InvalidValueException.blank は blank の details を持つ例外を生成する")
    void should_createBlankException_withExpectedDetails() {
        final InvalidValueException actual = InvalidValueException.blank("username");

        assertThat(actual.getErrorCode()).isEqualTo(ErrorCode.DOMAIN_INVALID_VALUE);
        assertThat(actual.getMessage()).isEqualTo("username is invalid. reason=blank");
        assertThat(actual.getDetails())
                .containsEntry("target", "username")
                .containsEntry("reason", "blank")
                .containsEntry("rejectedValue", "");
    }

    @Test
    @DisplayName("InvalidValueException.tooLong は maxLength を含む例外を生成する")
    void should_createTooLongException_withExpectedDetails() {
        final InvalidValueException actual = InvalidValueException.tooLong("comment", "abcdef", 5);

        assertThat(actual.getErrorCode()).isEqualTo(ErrorCode.DOMAIN_INVALID_VALUE);
        assertThat(actual.getMessage()).isEqualTo("comment length must be less than or equal to 5.");
        assertThat(actual.getDetails())
                .containsEntry("target", "comment")
                .containsEntry("reason", "too_long")
                .containsEntry("rejectedValue", "abcdef")
                .containsEntry("maxLength", 5);
    }

    @Test
    @DisplayName("InvalidValueException.tooShort は minLength を含む例外を生成する")
    void should_createTooShortException_withExpectedDetails() {
        final InvalidValueException actual = InvalidValueException.tooShort("password", "abc", 8);

        assertThat(actual.getErrorCode()).isEqualTo(ErrorCode.DOMAIN_INVALID_VALUE);
        assertThat(actual.getMessage()).isEqualTo("password length must be greater than or equal to 8.");
        assertThat(actual.getDetails())
                .containsEntry("target", "password")
                .containsEntry("reason", "too_short")
                .containsEntry("rejectedValue", "abc")
                .containsEntry("minLength", 8);
    }

    @Test
    @DisplayName("InvalidValueException.outOfRange は範囲情報を含む例外を生成する")
    void should_createOutOfRangeException_withExpectedDetails() {
        final InvalidValueException actual = InvalidValueException.outOfRange("pixel", 100_001, 0, 100_000);

        assertThat(actual.getErrorCode()).isEqualTo(ErrorCode.DOMAIN_INVALID_VALUE);
        assertThat(actual.getMessage()).isEqualTo("pixel must be between 0 and 100000.");
        assertThat(actual.getDetails())
                .containsEntry("target", "pixel")
                .containsEntry("reason", "out_of_range")
                .containsEntry("rejectedValue", 100_001)
                .containsEntry("min", 0)
                .containsEntry("max", 100_000);
    }

    @Test
    @DisplayName("InvalidValueException.invalidFormat は expectedFormat を含む例外を生成する")
    void should_createInvalidFormatException_withExpectedDetails() {
        final InvalidValueException actual = InvalidValueException.invalidFormat("email", "invalid", "@");

        assertThat(actual.getErrorCode()).isEqualTo(ErrorCode.DOMAIN_INVALID_VALUE);
        assertThat(actual.getMessage()).isEqualTo("email format is invalid.");
        assertThat(actual.getDetails())
                .containsEntry("target", "email")
                .containsEntry("reason", "invalid_format")
                .containsEntry("rejectedValue", "invalid")
                .containsEntry("expectedFormat", "@");
    }

    @Test
    @DisplayName("InvalidValueException.invalid は任意 reason を含む例外を生成する")
    void should_createInvalidException_withExpectedDetails() {
        final InvalidValueException actual = InvalidValueException.invalid(
                "object_key",
                "../secret",
                "cannot contain \"..\""
        );

        assertThat(actual.getErrorCode()).isEqualTo(ErrorCode.DOMAIN_INVALID_VALUE);
        assertThat(actual.getMessage()).isEqualTo("object_key is invalid. reason=cannot contain \"..\"");
        assertThat(actual.getDetails())
                .containsEntry("target", "object_key")
                .containsEntry("reason", "cannot contain \"..\"")
                .containsEntry("rejectedValue", "../secret");
    }
}
