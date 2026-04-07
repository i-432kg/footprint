package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 検索キーワードを表す値オブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchKeyword {

    /**
     * 最大文字数：100文字
     */
    static int MAX_LENGTH = 100;

    /**
     * 制御文字（U+0000-U+001F, U+007F）を検知するための正規表現
     */
    static Pattern CONTROL_CHARS_PATTERN = Pattern.compile(".*[\\x00-\\x1F\\x7F].*");

    static String FIELD_NAME = "search_keyword";

    String value;

    /**
     * 検索キーワードを指定して {@link SearchKeyword} インスタンスを生成します。
     *
     * @param value 検索キーワード
     * @return {@link SearchKeyword} インスタンス
     * @throws InvalidValueException バリデーションエラーの場合
     */
    public static SearchKeyword of(final @Nullable String value) {

        // null 禁止
        if (Objects.isNull(value)) {
            throw InvalidValueException.required(FIELD_NAME);
        }

        // 空文字のみを不許可
        if (value.isBlank()) {
            throw InvalidValueException.blank(FIELD_NAME);
        }

        // 最大文字数チェック
        if (value.length() > MAX_LENGTH) {
            throw InvalidValueException.tooLong(FIELD_NAME, value, MAX_LENGTH);
        }

        // 制御文字禁止
        if (CONTROL_CHARS_PATTERN.matcher(value).matches()) {
            throw InvalidValueException.invalidFormat(FIELD_NAME, value, CONTROL_CHARS_PATTERN.pattern());
        }

        return new SearchKeyword(value);
    }

    public String value() {
        return value;
    }
}
