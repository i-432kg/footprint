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

    /**
     * ULID の生成パターン
     */
    static Pattern ULID_PATTERN = Pattern.compile("^[0-9A-HJKMNP-TV-Z]{26}$");

    static String FIELD_NAME = "image_id";

    String value;

    public static ImageId of(String value) {

        // null 禁止
        if (Objects.isNull(value)) {
            throw InvalidValueException.required(FIELD_NAME);
        }

        final String normalized = value.trim();

        // 空文字のみを不許可
        if (normalized.isBlank()) {
            throw InvalidValueException.blank(FIELD_NAME);
        }

        if (!ULID_PATTERN.matcher(normalized).matches()) {
            throw InvalidValueException.invalidFormat(FIELD_NAME, normalized, ULID_PATTERN.pattern());
        }

        return new ImageId(normalized);
    }

    public String value() {
        return value;
    }
}
