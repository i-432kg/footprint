package jp.i432kg.footprint.domain.helper;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UlidValidationTest {

    private static final String FIELD_NAME = "post_id";
    private static final String VALID_ULID = "01ARZ3NDEKTSV4RRFFQ69G5FAV";

    @Test
    @DisplayName("UlidValidation.requireValidUlid は妥当な ULID をそのまま返す")
    void should_returnValue_when_valueIsValidUlid() {
        final String actual = UlidValidation.requireValidUlid(FIELD_NAME, VALID_ULID);

        assertThat(actual).isEqualTo(VALID_ULID);
    }

    @Test
    @DisplayName("UlidValidation.requireValidUlid は null を拒否する")
    void should_throwException_when_valueIsNull() {
        assertThatThrownBy(() -> UlidValidation.requireValidUlid(FIELD_NAME, null))
                .isInstanceOf(InvalidValueException.class)
                .satisfies(throwable -> {
                    final InvalidValueException exception = (InvalidValueException) throwable;
                    assertThat(exception.getDetails())
                            .containsEntry("target", FIELD_NAME)
                            .containsEntry("reason", "required");
                });
    }

    @Test
    @DisplayName("UlidValidation.requireValidUlid は空文字と空白のみ文字列を拒否する")
    void should_throwException_when_valueIsBlank() {
        assertBlank("");
        assertBlank("   ");
    }

    @Test
    @DisplayName("UlidValidation.requireValidUlid は ULID 形式でない値を拒否する")
    void should_throwException_when_valueHasInvalidFormat() {
        assertInvalidFormat("01ARZ3NDEKTSV4RRFFQ69G5FA");
        assertInvalidFormat("01ARZ3NDEKTSV4RRFFQ69G5FAVX");
        assertInvalidFormat("01arZ3NDEKTSV4RRFFQ69G5FAV");
        assertInvalidFormat("01ARZ3NDEKTSV4RRFFQ69G5FAI");
    }

    private static void assertBlank(final String value) {
        assertThatThrownBy(() -> UlidValidation.requireValidUlid(FIELD_NAME, value))
                .isInstanceOf(InvalidValueException.class)
                .satisfies(throwable -> {
                    final InvalidValueException exception = (InvalidValueException) throwable;
                    assertThat(exception.getDetails())
                            .containsEntry("target", FIELD_NAME)
                            .containsEntry("reason", "blank");
                });
    }

    private static void assertInvalidFormat(final String value) {
        assertThatThrownBy(() -> UlidValidation.requireValidUlid(FIELD_NAME, value))
                .isInstanceOf(InvalidValueException.class)
                .satisfies(throwable -> {
                    final InvalidValueException exception = (InvalidValueException) throwable;
                    assertThat(exception.getDetails())
                            .containsEntry("target", FIELD_NAME)
                            .containsEntry("reason", "invalid_format")
                            .containsEntry("rejectedValue", value);
                });
    }
}
