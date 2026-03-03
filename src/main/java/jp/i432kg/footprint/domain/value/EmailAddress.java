package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * ユーザーのメールアドレスを表す値オブジェクト。
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailAddress {

    String value;

    /**
     * メールアドレスを指定して {@link EmailAddress} インスタンスを生成します。
     *
     * @param value メールアドレス
     * @return {@link EmailAddress} インスタンス
     * @throws IllegalArgumentException 引数が null または空文字の場合
     */
    public static EmailAddress of(final String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("EmailAddress cannot be null or empty");
        }
        // TODO: 必要に応じて形式チェックの正規表現を追加
        return new EmailAddress(value);
    }

    public String value() {
        return value;
    }
}
