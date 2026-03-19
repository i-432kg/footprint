package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import lombok.*;

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
    private static final int MAX_LENGTH = 10;

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
    public static FileExtension of(final String value) {

        if (Objects.isNull(value)) {
            throw new InvalidValueException("common.invalid.null", "field.extension");
        }

        final String normalized = normalize(value);

        if (normalized.isEmpty()) {
            throw new InvalidValueException("common.invalid.blank", "field.extension");
        }

        if (normalized.length() > MAX_LENGTH) {
            throw new InvalidValueException("common.invalid.length", "field.extension", MAX_LENGTH);
        }

        if (!normalized.matches("^[a-z0-9]+$")) {
            throw new InvalidValueException("common.invalid.chars", "field.extension", normalized);
        }

        return Allowed.fromString(normalized)
                .map(allowed -> new FileExtension(allowed.getValue()))
                .orElseThrow(() -> new InvalidValueException("extension.invalid.unsupported", normalized));
    }

    /**
     * ドット付きの拡張子表現を返します。
     *
     * @return 例: ".jpg"
     */
    public String withDot() {
        return "." + value;
    }

    public String value() {
        return value;
    }

    private static String normalize(final String value) {
        final String trimmed = value.strip().toLowerCase(Locale.ROOT);
        return trimmed.startsWith(".") ? trimmed.substring(1) : trimmed;
    }
}
