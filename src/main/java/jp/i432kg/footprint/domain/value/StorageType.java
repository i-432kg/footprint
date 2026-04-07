package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum StorageType {

    LOCAL("LOCAL"),
    S3("S3");

    private final String value;

    public static StorageType of(final @Nullable String value) {
        return Arrays.stream(values())
                .filter(v -> v.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> InvalidValueException.invalid(
                        "storage_type",
                        Objects.requireNonNull(value),
                        "unknown storage type"
                ));
    }
}
