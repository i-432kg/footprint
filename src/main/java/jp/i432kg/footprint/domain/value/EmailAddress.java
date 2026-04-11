package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.jspecify.annotations.Nullable;

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
    static int MAX_LENGTH = 254;

    /**
     * ローカル部の最大文字長：64文字
     */
    static int LOCAL_PART_MAX_LENGTH = 64;

    /**
     * ローカル部の許可文字パターン
     * 英数 + .!#$%&'*+/=?^_{|}~-`
     */
    static String LOCAL_PART_PATTERN = "[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+";

    /**
     * ドメイン部の許可文字パターン
     * 英数・ハイフン・ドット（ASCIIのみ）
     */
    static String DOMAIN_PART_PATTERN = "[a-zA-Z0-9.-]+";

    static String FIELD_NAME = "email";

    String value;

    /**
     * メールアドレスを指定して {@link EmailAddress} インスタンスを生成します。
     *
     * @param value メールアドレス
     * @return {@link EmailAddress} インスタンス
     * @throws InvalidValueException バリデーションエラーの場合
     */
    public static EmailAddress of(final @Nullable String value) {

        // null 禁止
        if (Objects.isNull(value)) {
            throw InvalidValueException.required(FIELD_NAME);
        }

        // 空白・改行のトリムおよび小文字化
        final String normalized = value.strip().toLowerCase();

        // 空文字のみを不許可
        if (normalized.isBlank()) {
            throw InvalidValueException.blank(value);
        }

        // 最大文字数のチェック
        if (normalized.length() > MAX_LENGTH) {
            throw InvalidValueException.tooLong(FIELD_NAME, value, MAX_LENGTH);
        }

        // @ の個数チェック
        final String[] parts = normalized.split("@");
        if (parts.length != 2) {
            throw InvalidValueException.invalidFormat(FIELD_NAME, value, "@");
        }

        // ローカル部のバリデーション
        final String localPart = parts[0];
        validateLocalPart(localPart);

        // ドメイン部のバリデーション
        final String domainPart = parts[1];
        validateDomainPart(domainPart);

        return new EmailAddress(normalized);
    }
    /**
     * ローカル部の形式を検証します。
     *
     * @param localPart 検証対象のローカル部
     */
    private static void validateLocalPart(final String localPart) {

        final String FIELD_NAME = "email_local_part";

        if (localPart.isBlank()) {
            throw InvalidValueException.blank(FIELD_NAME);
        }
        if (localPart.length() > LOCAL_PART_MAX_LENGTH) {
            throw InvalidValueException.tooLong(FIELD_NAME, localPart, LOCAL_PART_MAX_LENGTH);
        }
        if (!localPart.matches(LOCAL_PART_PATTERN)) {
            throw InvalidValueException.invalidFormat(FIELD_NAME, localPart, LOCAL_PART_PATTERN);
        }
        // ドットの連続、先頭末尾禁止
        if (localPart.startsWith(".") || localPart.endsWith(".") || localPart.contains("..")) {
            throw InvalidValueException.invalid(FIELD_NAME, localPart, "cannot start or end with a dot");
        }
    }

    /**
     * ドメイン部の形式を検証します。
     *
     * @param domainPart 検証対象のドメイン部
     */
    private static void validateDomainPart(final String domainPart) {

        final String FIELD_NAME = "email_domain_part";

        if (domainPart.isBlank()) {
            throw InvalidValueException.blank(FIELD_NAME);
        }
        if (!domainPart.matches(DOMAIN_PART_PATTERN)) {
            throw InvalidValueException.invalidFormat(FIELD_NAME, domainPart, DOMAIN_PART_PATTERN);
        }
        // ドットが少なくとも1つある、ドットの連続禁止、先頭末尾禁止
        if (!domainPart.contains(".") || domainPart.startsWith(".") || domainPart.endsWith(".") || domainPart.contains("..")) {
            throw InvalidValueException.invalid(FIELD_NAME, domainPart, "must contain at least one dot");
        }
    }
}
