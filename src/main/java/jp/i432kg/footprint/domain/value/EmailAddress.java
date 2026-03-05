package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Objects;

/**
 * ユーザーのメールアドレスを表す値オブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailAddress {

    /**
     * 最大文字長：254文字
     */
    private static final int MAX_LENGTH = 254;

    /**
     * ローカル部の最大文字長：64文字
     */
    private static final int LOCAL_PART_MAX_LENGTH = 64;

    /**
     * ローカル部の許可文字パターン
     * 英数 + .!#$%&'*+/=?^_{|}~-`
     */
    private static final String LOCAL_PART_PATTERN = "[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+";

    /**
     * ドメイン部の許可文字パターン
     * 英数・ハイフン・ドット（ASCIIのみ）
     */
    private static final String DOMAIN_PART_PATTERN = "[a-zA-Z0-9.-]+";

    String value;

    /**
     * メールアドレスを指定して {@link EmailAddress} インスタンスを生成します。
     *
     * @param value メールアドレス
     * @return {@link EmailAddress} インスタンス
     * @throws IllegalArgumentException バリデーションエラーの場合
     */
    public static EmailAddress of(final String value) {

        // null 禁止
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("EmailAddress cannot be null");
        }

        // 空白・改行のトリムおよび小文字化
        final String normalized = value.strip().toLowerCase();

        // 空文字のみを不許可
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("EmailAddress cannot be empty");
        }

        // 最大文字数のチェック
        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("EmailAddress exceeds the limit of " + MAX_LENGTH + " characters");
        }

        // @ の個数チェック
        final String[] parts = normalized.split("@");
        if (parts.length != 2) {
            throw new IllegalArgumentException("EmailAddress must contain exactly one '@'");
        }

        // ローカル部のバリデーション
        final String localPart = parts[0];
        validateLocalPart(localPart);

        // ドメイン部のバリデーション
        final String domainPart = parts[1];
        validateDomainPart(domainPart);

        return new EmailAddress(normalized);
    }

    public String value() {
        return value;
    }

    /**
     * ローカル部の形式を検証します。
     *
     * @param localPart 検証対象のローカル部
     */
    private static void validateLocalPart(final String localPart) {
        if (localPart.isBlank() || localPart.length() > LOCAL_PART_MAX_LENGTH) {
            throw new IllegalArgumentException("Local part is too long or empty");
        }
        if (!localPart.matches(LOCAL_PART_PATTERN)) {
            throw new IllegalArgumentException("Local part contains invalid characters");
        }
        // ドットの連続、先頭末尾禁止
        if (localPart.startsWith(".") || localPart.endsWith(".") || localPart.contains("..")) {
            throw new IllegalArgumentException("Local part has invalid dot placement");
        }
    }

    /**
     * ドメイン部の形式を検証します。
     *
     * @param domainPart 検証対象のドメイン部
     */
    private static void validateDomainPart(final String domainPart) {
        if (domainPart.isBlank()) {
            throw new IllegalArgumentException("Domain part cannot be empty");
        }
        if (!domainPart.matches(DOMAIN_PART_PATTERN)) {
            throw new IllegalArgumentException("Domain part contains invalid characters");
        }
        // ドットが少なくとも1つある、ドットの連続禁止、先頭末尾禁止
        if (!domainPart.contains(".") || domainPart.startsWith(".") || domainPart.endsWith(".") || domainPart.contains("..")) {
            throw new IllegalArgumentException("Domain part is invalid or has invalid dot placement");
        }
    }
}
