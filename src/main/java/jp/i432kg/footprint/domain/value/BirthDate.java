package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BirthDate {

    LocalDate value;

    public static BirthDate of(final LocalDate value) {
        if (value == null) {
            throw new IllegalArgumentException("BirthDate cannot be null");
        }
        return new BirthDate(value);
    }

    public LocalDate value() {
        return value;
    }
}
