package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Locale;
import java.util.Objects;

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
     * 拡張子を指定して {@link FileExtension} インスタンスを生成します。
     *
     * @param value 拡張子
     * @return {@link FileExtension} インスタンス
     * @throws IllegalArgumentException バリデーションエラーの場合
     */
    public static FileExtension of(final String value) {

        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("Extension cannot be null");
        }

        final String normalized = normalize(value);

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Extension cannot be empty");
        }

        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Extension exceeds the limit of " + MAX_LENGTH + " characters");
        }

        if (!normalized.matches("^[a-z0-9]+$")) {
            throw new IllegalArgumentException("Extension contains invalid characters: " + normalized);
        }

        return new FileExtension(normalized);
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
