package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchKeyword {

    String value;

    /**
     * 検索キーワードを指定して {@link SearchKeyword} インスタンスを生成します。
     *
     * @param value 検索キーワード
     * @return {@link SearchKeyword} インスタンス
     */
    public static SearchKeyword of(final String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Search keyword cannot be null or empty");
        }
        return new SearchKeyword(value);
    }

    public String value() {
        return value;
    }
}
