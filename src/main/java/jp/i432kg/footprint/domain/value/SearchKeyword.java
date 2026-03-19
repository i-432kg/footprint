package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * 検索キーワードを表す値オブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchKeyword {

    /**
     * 最大文字数：100文字
     */
    private static final int MAX_LENGTH = 100;

    /**
     * 制御文字（U+0000-U+001F, U+007F）を検知するための正規表現
     */
    private static final String CONTROL_CHARS_PATTERN = ".*[\\x00-\\x1F\\x7F].*";

    String value;

    /**
     * 検索キーワードを指定して {@link SearchKeyword} インスタンスを生成します。
     *
     * @param value 検索キーワード
     * @return {@link SearchKeyword} インスタンス
     * @throws InvalidValueException バリデーションエラーの場合
     */
    public static SearchKeyword of(final String value) {

        // null 禁止
        if (value == null) {
            throw new InvalidValueException("common.invalid.null", "field.search_keyword");
        }

        // 空白・改行のみを禁止
        if (value.isBlank()) {
            throw new InvalidValueException("common.invalid.blank", "field.search_keyword");
        }

        // 最大文字数チェック
        if (value.length() > MAX_LENGTH) {
            throw new InvalidValueException("commnon.invalid.length", MAX_LENGTH, "field.search_keyword");
        }

        // 制御文字禁止
        if (value.matches(CONTROL_CHARS_PATTERN)) {
            throw new InvalidValueException("searchkeyword.invalid.control.chars", "field.search_keyword");
        }

        return new SearchKeyword(value);
    }

    public String value() {
        return value;
    }
}
