package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import lombok.*;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * ファイル拡張子を表す値オブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FileExtension {

    /**
     * 拡張子の最大文字数
     */
    static int MAX_LENGTH = 10;

    /**
     * 許可文字パターン
     */
    static String ALLOWED_PATTERN = "^[a-z0-9]+$";

    static String FIELD_NAME = "extension";

    String value;

    /**
     * 許可された拡張子の定義
     */
    @Getter
    @RequiredArgsConstructor
    public enum Allowed {
        JPG("jpg"),
        JPEG("jpeg"),
        PNG("png"),
        GIF("gif"),
        WEBP("webp");

        private final String value;

        public static Optional<Allowed> fromString(final String extension) {
            return Arrays.stream(values())
                    .filter(e -> e.value.equalsIgnoreCase(extension))
                    .findFirst();
        }
    }

    /**
     * 拡張子を指定して {@link FileExtension} インスタンスを生成します。
     *
     * @param value 拡張子
     * @return {@link FileExtension} インスタンス
     * @throws InvalidValueException バリデーションエラーの場合
     */
    public static FileExtension of(final @Nullable String value) {

        // null 禁止
        if (Objects.isNull(value)) {
            throw InvalidValueException.required(FIELD_NAME);
        }

        final String normalized = normalize(value);

        if (normalized.isEmpty()) {
            throw InvalidValueException.blank(FIELD_NAME);
        }

        if (normalized.length() > MAX_LENGTH) {
            throw InvalidValueException.tooLong(FIELD_NAME, normalized, MAX_LENGTH);
        }

        if (!normalized.matches(ALLOWED_PATTERN)) {
            throw InvalidValueException.invalidFormat(FIELD_NAME, normalized, ALLOWED_PATTERN);
        }

        return Allowed.fromString(normalized)
                .map(allowed -> new FileExtension(allowed.getValue()))
                .orElseThrow(() -> InvalidValueException.invalid(FIELD_NAME, normalized, "unsupported extension"));
    }

    /**
     * ドット付きの拡張子表現を返します。
     *
     * @return 例: ".jpg"
     */
    public String withDot() {
        return "." + value;
    }

    private static String normalize(final String value) {
        final String trimmed = value.strip().toLowerCase(Locale.ROOT);
        return trimmed.startsWith(".") ? trimmed.substring(1) : trimmed;
    }
}
