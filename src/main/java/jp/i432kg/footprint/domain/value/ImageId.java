package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 画像を一意に識別するための ID を表す値オブジェクト
 * <p>
 * フロント側に公開する ID として扱う
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageId {

    static final Pattern ULID_PATTERN =
            Pattern.compile("^[0-9A-HJKMNP-TV-Z]{26}$");

    String value;

    public static ImageId of(String value) {

        if (Objects.isNull(value)) {
            throw new InvalidValueException("common.invalid.null", "field.imageId");
        }

        final String normalized = value.trim();

        if (normalized.isEmpty()) {
            throw new InvalidValueException("common.invalid.blank", "field.imageId");
        }

        if (!ULID_PATTERN.matcher(normalized).matches()) {
            throw new InvalidValueException("common.invalid.format", "field.imageId");
        }

        return new ImageId(normalized);
    }

    public String value() {
        return value;
    }
}
