package jp.i432kg.footprint.domain.exception;

import jp.i432kg.footprint.exception.ErrorCode;

import java.util.Map;

/**
 * 値オブジェクトのバリデーションエラー（ドメインの不変条件違反）を示す例外
 */
public class InvalidValueException extends DomainException {

    private InvalidValueException(String message, Map<String, Object> details) {
        super(ErrorCode.DOMAIN_INVALID_VALUE, message, details);
    }

    public static InvalidValueException required(String target) {
        return new InvalidValueException(
                message(target, "required"),
                details(target, "required", null)
        );
    }

    public static InvalidValueException blank(String target) {
        return new InvalidValueException(
                message(target, "blank"),
                details(target, "blank", "")
        );
    }

    public static InvalidValueException tooLong(String target, Object rejectedValue, int maxLength) {
        return new InvalidValueException(
                target + " length must be less than or equal to " + maxLength + ".",
                details(target, "too_long", rejectedValue, Map.of("maxLength", maxLength))
        );
    }

    public static InvalidValueException tooShort(String target, Object rejectedValue, int minLength) {
        return new InvalidValueException(
                target + " length must be greater than or equal to " + minLength + ".",
                details(target, "too_short", rejectedValue, Map.of("minLength", minLength))
        );
    }

    public static InvalidValueException outOfRange(
            String target,
            Object rejectedValue,
            Number min,
            Number max
    ) {
        return new InvalidValueException(
                target + " must be between " + min + " and " + max + ".",
                details(target, "out_of_range", rejectedValue, Map.of("min", min, "max", max))
        );
    }

    public static InvalidValueException invalidFormat(String target, Object rejectedValue, String expectedFormat) {
        return new InvalidValueException(
                target + " format is invalid.",
                details(target, "invalid_format", rejectedValue, Map.of("expectedFormat", expectedFormat))
        );
    }

    public static InvalidValueException invalid(String target, Object rejectedValue, String reason) {
        return new InvalidValueException(
                message(target, reason),
                details(target, reason, rejectedValue)
        );
    }
}