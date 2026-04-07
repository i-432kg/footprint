package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * ファイル名を表す値オブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FileName {


    /**
     * ファイル名の最大文字数
     */
    static int MAX_LENGTH = 254;

    /**
     * 禁止文字の正規表現（OS/URL危険文字）
     * / \ : * ? " < > |
     */
    static String INVALID_CHARS_PATTERN = ".*[/\\\\:*?\"<>|].*";

    static String FIELD_NAME = "filename";

    String value;

    /**
     * ファイル名を指定して {@link FileName} インスタンスを生成します。
     *
     * @param value ファイル名
     * @return {@link FileName} インスタンス
     * @throws InvalidValueException バリデーションエラーの場合
     */
    public static FileName of(final @Nullable String value) {

        // null 禁止
        if (Objects.isNull(value)) {
            throw InvalidValueException.required(FIELD_NAME);
        }

        // 空文字のみを不許可
        if (value.isBlank()) {
            throw InvalidValueException.blank(FIELD_NAME);
        }

        // 文字数上限チェック
        if (value.length() > MAX_LENGTH) {
            throw InvalidValueException.tooLong(FIELD_NAME, value, MAX_LENGTH);
        }

        // 禁止文字（OS/URL危険文字）チェック
        if (value.matches(INVALID_CHARS_PATTERN)) {
            throw InvalidValueException.invalidFormat(FIELD_NAME, value, INVALID_CHARS_PATTERN);
        }

        // パストラバーサル要素（..）の禁止
        if (value.contains("..")) {
            throw InvalidValueException.invalid(FIELD_NAME, value, "cannot contain \"..\"");
        }

        return new FileName(value);
    }

    public String value() {
        return value;
    }
}
