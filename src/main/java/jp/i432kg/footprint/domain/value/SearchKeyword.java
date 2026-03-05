package jp.i432kg.footprint.domain.value;

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
     * @throws IllegalArgumentException バリデーションエラーの場合
     */
    public static SearchKeyword of(final String value) {

        // null 禁止
        if (value == null) {
            throw new IllegalArgumentException("Search keyword cannot be null");
        }

        // 空白・改行のみを禁止
        if (value.isBlank()) {
            throw new IllegalArgumentException("Search keyword cannot be empty or blank");
        }

        // 最大文字数チェック
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Search keyword exceeds the limit of " + MAX_LENGTH + " characters");
        }

        // 制御文字禁止
        if (value.matches(CONTROL_CHARS_PATTERN)) {
            throw new IllegalArgumentException("Search keyword contains invalid control characters");
        }

        return new SearchKeyword(value);
    }

    public String value() {
        return value;
    }
}
