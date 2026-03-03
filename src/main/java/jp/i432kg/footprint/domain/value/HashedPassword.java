package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * ハッシュ化済みのパスワードを表す値オブジェクト。
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HashedPassword {

    String value;

    /**
     * ハッシュ化済みパスワードを指定して {@link HashedPassword} インスタンスを生成します。
     *
     * @param value ハッシュ化済みパスワード
     * @return {@link HashedPassword} インスタンス
     * @throws IllegalArgumentException 引数が null または空文字の場合
     */
    public static HashedPassword of(final String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Hashed password cannot be null or empty");
        }
        return new HashedPassword(value);
    }

    public String value() {
        return value;
    }
}