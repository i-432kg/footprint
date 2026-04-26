package jp.i432kg.footprint.domain.exception;

import jp.i432kg.footprint.exception.ErrorCode;

import java.util.Map;

/**
 * 値オブジェクト単位の検証失敗を表す例外です。
 * <p>
 * 必須チェック、文字数制約、範囲制約、書式制約など、
 * 単一値に対する不正を表現するために利用します。
 */
public class InvalidValueException extends DomainException {

    private InvalidValueException(final String message, final Map<String, Object> details) {
        super(ErrorCode.DOMAIN_INVALID_VALUE, message, details);
    }

    /**
     * 必須値が与えられていないことを表す例外を生成します。
     *
     * @param target 問題対象
     * @return 生成した例外
     */
    public static InvalidValueException required(final String target) {
        return new InvalidValueException(
                message(target, "required"),
                details(target, "required", "null")
        );
    }

    /**
     * 空文字が不正であることを表す例外を生成します。
     *
     * @param target 問題対象
     * @return 生成した例外
     */
    public static InvalidValueException blank(final String target) {
        return new InvalidValueException(
                message(target, "blank"),
                details(target, "blank", "")
        );
    }

    /**
     * 文字数が上限を超えていることを表す例外を生成します。
     *
     * @param target 問題対象
     * @param rejectedValue 問題となった値
     * @param maxLength 許容最大文字数
     * @return 生成した例外
     */
    public static InvalidValueException tooLong(
            final String target,
            final Object rejectedValue,
            final int maxLength
    ) {
        return new InvalidValueException(
                target + " length must be less than or equal to " + maxLength + ".",
                details(target, "too_long", rejectedValue, Map.of("maxLength", maxLength))
        );
    }

    /**
     * 文字数が下限を下回っていることを表す例外を生成します。
     *
     * @param target 問題対象
     * @param rejectedValue 問題となった値
     * @param minLength 許容最小文字数
     * @return 生成した例外
     */
    public static InvalidValueException tooShort(
            final String target,
            final Object rejectedValue,
            final int minLength
    ) {
        return new InvalidValueException(
                target + " length must be greater than or equal to " + minLength + ".",
                details(target, "too_short", rejectedValue, Map.of("minLength", minLength))
        );
    }

    /**
     * 数値や座標などが許容範囲外であることを表す例外を生成します。
     *
     * @param target 問題対象
     * @param rejectedValue 問題となった値
     * @param min 許容最小値
     * @param max 許容最大値
     * @return 生成した例外
     */
    public static InvalidValueException outOfRange(
            final String target,
            final Object rejectedValue,
            final Number min,
            final Number max
    ) {
        return new InvalidValueException(
                target + " must be between " + min + " and " + max + ".",
                details(target, "out_of_range", rejectedValue, Map.of("min", min, "max", max))
        );
    }

    /**
     * 書式が不正であることを表す例外を生成します。
     *
     * @param target 問題対象
     * @param rejectedValue 問題となった値
     * @param expectedFormat 期待書式
     * @return 生成した例外
     */
    public static InvalidValueException invalidFormat(
            final String target,
            final Object rejectedValue,
            final String expectedFormat
    ) {
        return new InvalidValueException(
                target + " format is invalid.",
                details(target, "invalid_format", rejectedValue, Map.of("expectedFormat", expectedFormat))
        );
    }

    /**
     * 任意の不正理由を持つ例外を生成します。
     *
     * @param target 問題対象
     * @param rejectedValue 問題となった値
     * @param reason 不正理由
     * @return 生成した例外
     */
    public static InvalidValueException invalid(
            final String target,
            final Object rejectedValue,
            final String reason
    ) {
        return new InvalidValueException(
                message(target, reason),
                details(target, reason, rejectedValue)
        );
    }
}
