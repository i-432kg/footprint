package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RawPassword {

    static final int MAX_LENGTH = 128;
    static final int MIN_LENGTH = 15;

    String value;

    /**
     * 生パスワードを指定して {@link RawPassword} インスタンスを生成します。
     *
     * @param value パスワード文字列
     * @return {@link RawPassword} インスタンス
     */
    public static RawPassword of(final String value) {
//        if (value == null || value.isBlank()) {
//            throw new IllegalArgumentException("Password cannot be null or empty");
//        }
//        // バリデーション例
//        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
//            throw new IllegalArgumentException("Password must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters");
//        }

        return new RawPassword(value);
    }

    public String value() {
        return value;
    }
}