package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import jp.i432kg.footprint.exception.ErrorCode;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;

import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class ValueObjectTestSupport {

    static final String VALID_ULID = "01ARZ3NDEKTSV4RRFFQ69G5FAV";

    private ValueObjectTestSupport() {
    }

    static void assertInvalidValue(
            final ThrowingCallable actual,
            final String target,
            final String reason
    ) {
        assertInvalidValue(actual, target, reason, details -> {
        });
    }

    static void assertInvalidValue(
            final ThrowingCallable actual,
            final String target,
            final String reason,
            final Consumer<Map<String, Object>> detailsAssertion
    ) {
        assertThatThrownBy(actual)
                .isInstanceOf(InvalidValueException.class)
                .satisfies(throwable -> {
                    final InvalidValueException exception = (InvalidValueException) throwable;
                    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DOMAIN_INVALID_VALUE);
                    assertThat(exception.getDetails())
                            .containsEntry("target", target)
                            .containsEntry("reason", reason);
                    detailsAssertion.accept(exception.getDetails());
                });
    }
}
