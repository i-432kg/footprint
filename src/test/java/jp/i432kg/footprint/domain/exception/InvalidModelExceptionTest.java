package jp.i432kg.footprint.domain.exception;

import jp.i432kg.footprint.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidModelExceptionTest {

    @Test
    @DisplayName("InvalidModelException.invalid はモデル不変条件違反の details を持つ例外を生成する")
    void should_createInvalidException_withExpectedDetails() {
        final InvalidModelException actual = InvalidModelException.invalid(
                "image",
                319,
                "short_side_pixels_too_small"
        );

        assertThat(actual.getErrorCode()).isEqualTo(ErrorCode.DOMAIN_INVALID_MODEL);
        assertThat(actual.getMessage()).isEqualTo("image is invalid. reason=short_side_pixels_too_small");
        assertThat(actual.getDetails())
                .containsEntry("target", "image")
                .containsEntry("reason", "short_side_pixels_too_small")
                .containsEntry("rejectedValue", 319);
    }
}
