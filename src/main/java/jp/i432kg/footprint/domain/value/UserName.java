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

    public static UserName of(final String value) {
        return new UserName(value);
    }

    public String value() {
        return value;
    }
}