package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserName {

    static final int MAX_LENGTH = 30;
    static final int MIN_LENGTH = 4;

    String value;

    /**
     * ユーザー名を指定して {@link UserName} インスタンスを生成します。
     *
     * @param value ユーザー名
     * @return {@link UserName} インスタンス
     */
    public static UserName of(final String value) {
//        if (value == null || value.isBlank()) {
//            throw new IllegalArgumentException("UserName cannot be null or empty");
//        }
//        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
//            throw new IllegalArgumentException("UserName must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters");
//        }
        return new UserName(value);
    }

    public String value() {
        return value;
    }
}