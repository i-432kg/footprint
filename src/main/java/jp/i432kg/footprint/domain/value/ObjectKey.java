package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import lombok.*;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * を表す値オブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectKey {

    /**
     * 最大文字数：1024文字
     * <p>
     * S3 キーの最大長 1024 byte を上限とする。
     */
    static int MAX_LENGTH = 1024;

    /**
     * 許可文字パターン
     * 英数 + /._-
     * <p>
     * 例:
     * users/123/posts/456/images/abc123.jpg
     */
    static Pattern ALLOWED_PATTERN = Pattern.compile("^[A-Za-z0-9._/-]+$");

    static String FIELD_NAME = "object_key";

    String value;

    public static ObjectKey of(final String value) {

        // null 禁止
        if (Objects.isNull(value)) {
            throw InvalidValueException.required(FIELD_NAME);
        }

        // 正規化
        final String normalized = value.trim();

        // 空文字チェック
        if (normalized.isEmpty()) {
            throw InvalidValueException.blank(FIELD_NAME);
        }

        // 最大長チェック
        if (normalized.length() > MAX_LENGTH) {
            throw InvalidValueException.tooLong(FIELD_NAME, normalized, MAX_LENGTH);
        }

        // 絶対パス攻撃の防止チェック
        if (normalized.startsWith("/")) {
            throw InvalidValueException.invalid(FIELD_NAME, normalized, "cannot start with \"/\"");
        }

        // Windows パス混入のチェック
        if (normalized.contains("\\")) {
            throw InvalidValueException.invalid(FIELD_NAME, normalized, "cannot contain \"\\\\\"");
        }

        // 許可文字チェック
        if (!ALLOWED_PATTERN.matcher(normalized).matches()) {
            throw InvalidValueException.invalidFormat(FIELD_NAME, normalized, ALLOWED_PATTERN.pattern());
        }

        // パスの正規化
        if (normalized.contains("//")) {
            throw InvalidValueException.invalid(FIELD_NAME, normalized, "cannot contain \"//\"");
        }

        // パストラバーサル攻撃の防止チェック
        final String[] segments = normalized.split("/");
        for (final String segment : segments) {
            if (segment.equals(".") || segment.equals("..") || segment.isEmpty()) {
                throw InvalidValueException.invalid(FIELD_NAME, normalized, "cannot contain \".\" or \"..\" or empty segment");
            }
        }

        // ディレクトリとして保存禁止のチェック
        if (normalized.endsWith("/")) {
            throw InvalidValueException.invalid(FIELD_NAME, normalized, "cannot end with \"/\"");
        }

        return new ObjectKey(normalized);
    }
}