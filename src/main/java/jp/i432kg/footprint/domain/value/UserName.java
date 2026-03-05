package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Objects;

/**
 * ユーザー名を表す値オブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserName {

    /**
     * 最小文字数：4文字
     */
    private static final int MIN_LENGTH = 4;

    /**
     * 最大文字数：15文字
     */
    private static final int MAX_LENGTH = 15;

    /**
     * 英数および記号のみを許可し、空白文字を含まないパターン
     * \x21-\x7E は ASCII の可視文字（スペースを除く）
     */
    private static final String ALLOWED_PATTERN = "^[\\x21-\\x7E]+$";

    String value;

    /**
     * ユーザー名を指定して {@link UserName} インスタンスを生成します。
     *
     * @param value ユーザー名
     * @return {@link UserName} インスタンス
     * @throws IllegalArgumentException バリデーションエラーの場合
     */
    public static UserName of(final String value) {

        // null 禁止
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("UserName cannot be null");
        }

        // 空白・改行のトリム
        final String trimmed = value.strip();

        // 文字数チェック
        if (trimmed.length() < MIN_LENGTH || trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("UserName must be between %d and %d characters: %s", MIN_LENGTH, MAX_LENGTH, trimmed)
            );
        }

        // 英数記号のみ（空白・制御文字不可）のチェック
        if (!trimmed.matches(ALLOWED_PATTERN)) {
            throw new IllegalArgumentException("UserName contains invalid characters. Only alphanumeric and symbols are allowed.");
        }

        return new UserName(trimmed);
    }

    public String value() {
        return value;
    }
}