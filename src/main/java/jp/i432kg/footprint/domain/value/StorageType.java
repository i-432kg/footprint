package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum StorageType {
    LOCAL("LOCAL"),
    S3("S3");

    private final String value;

    public static StorageType of(final String value) {
        return Arrays.stream(values())
                .filter(v -> v.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException("storage.type.invalid.unknown", value));
    }
}