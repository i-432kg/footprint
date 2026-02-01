package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HashedPassword {

    String value;

    public static HashedPassword of(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("Hashed password cannot be null");
        }
        return new HashedPassword(value);
    }

    public String value() {
        return value;
    }
}