package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.DomainException;
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
    private static final int MAX_LENGTH = 1024;

    /**
     * 許可文字パターン
     * 英数 + /._-
     * <p>
     * 例:
     * users/123/posts/456/images/abc123.jpg
     */
    private static final Pattern ALLOWED_PATTERN = Pattern.compile("^[A-Za-z0-9._/-]+$");

    String value;

    public static ObjectKey of(final String value) {

        // null 禁止
        if (Objects.isNull(value)) {
            throw new DomainException("objectKey must not be null.");
        }

        // 正規化
        final String normalized = value.trim();

        // 空文字チェック
        if (normalized.isEmpty()) {
            throw new DomainException("objectKey must not be empty.");
        }

        // 最大長チェック
        if (normalized.length() > MAX_LENGTH) {
            throw new DomainException("objectKey length must be less than or equal to " + MAX_LENGTH + ".");
        }

        // 絶対パス攻撃の防止チェック
        if (normalized.startsWith("/")) {
            throw new DomainException("objectKey must not start with '/'.");
        }

        // Windows パス混入のチェック
        if (normalized.contains("\\")) {
            throw new DomainException("objectKey must not contain '\\'.");
        }

        // 許可文字チェック
        if (!ALLOWED_PATTERN.matcher(normalized).matches()) {
            throw new DomainException("objectKey contains invalid characters.");
        }

        // パスの正規化
        if (normalized.contains("//")) {
            throw new DomainException("objectKey must not contain '//'.");
        }

        // パストラバーサル攻撃の防止チェック
        final String[] segments = normalized.split("/");
        for (final String segment : segments) {
            if (segment.equals(".") || segment.equals("..")) {
                throw new DomainException("objectKey must not contain '.' or '..' path segments.");
            }
            if (segment.isEmpty()) {
                throw new DomainException("objectKey must not contain empty path segments.");
            }
        }

        // ディレクトリとして保存禁止のチェック
        if (normalized.endsWith("/")) {
            throw new DomainException("objectKey must not end with '/'.");
        }

        return new ObjectKey(normalized);
    }
}