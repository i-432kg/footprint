package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FilePath {

    String value;

    public static FilePath of(final String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Image file path cannot be null or empty");
        }
        return new FilePath(value);
    }

    public String value() {
        return value;
    }
}