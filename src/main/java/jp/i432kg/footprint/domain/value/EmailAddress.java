package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailAddress {

    String value;

    public static EmailAddress of(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("EmailAddress cannot be null");
        }
        return new EmailAddress(value);
    }

    public String value() {
        return value;
    }
}
