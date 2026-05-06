package jp.i432kg.footprint.domain.helper;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * ULID 形式の値を検証するための共通ヘルパーです。
 * <p>
 * ID 系値オブジェクトで共通となる null / blank / 形式検証をまとめます。
 */
public final class UlidValidation {

    /**
     * ULID の生成パターン
     */
    private static final Pattern ULID_PATTERN = Pattern.compile("^[0-9A-HJKMNP-TV-Z]{26}$");

    private UlidValidation() {
    }

    /**
     * 必須の ULID 文字列を検証し、検証済みの値を返します。
     *
     * @param fieldName バリデーション対象のフィールド名
     * @param value 検証対象の値
     * @return 検証済みの ULID 文字列
     * @throws InvalidValueException 値が null、blank、または ULID 形式でない場合
     */
    public static String requireValidUlid(final String fieldName, final @Nullable String value) {
        if (Objects.isNull(value)) {
            throw InvalidValueException.required(fieldName);
        }

        if (value.isBlank()) {
            throw InvalidValueException.blank(fieldName);
        }

        if (!ULID_PATTERN.matcher(value).matches()) {
            throw InvalidValueException.invalidFormat(fieldName, value, ULID_PATTERN.pattern());
        }

        return value;
    }
}
