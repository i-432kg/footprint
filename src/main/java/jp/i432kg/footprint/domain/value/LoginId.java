package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * ユーザーのログイン ID を表す値オブジェクト。
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginId {

    String value;

    /**
     * ログイン ID を指定して {@link LoginId} インスタンスを生成します。
     *
     * @param value ログイン ID
     * @return {@link LoginId} インスタンス
     * @throws IllegalArgumentException 引数が null または空文字の場合
     */
    public static LoginId of(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("LoginId cannot be null");
        }
        return new LoginId(value);
    }

    public String value() {
        return value;
    }
}
