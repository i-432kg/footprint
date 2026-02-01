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

    public static RawPassword of(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        return new RawPassword(value);
    }

    public String value() {
        return value;
    }
}