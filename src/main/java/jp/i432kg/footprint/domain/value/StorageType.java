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

    static String FIELD_NAME = "storage_type";

    private final String value;

    public static StorageType of(final @Nullable String value) {

        if (Objects.isNull(value)) {
            throw InvalidValueException.required(FIELD_NAME);
        }

        return Arrays.stream(values())
                .filter(v -> v.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> InvalidValueException.invalid(
                        FIELD_NAME,
                        value,
                        "unknown storage type"
                ));
    }
}
