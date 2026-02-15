package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginId {

    String value;

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
