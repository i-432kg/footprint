package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Value;

/**
 * 生パスワードを表す値オブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(onlyExplicitlyIncluded = true)
public class RawPassword {

    /**
     * 最小文字数
     */
    private static final int MIN_LENGTH = 8;

    /**
     * 最大文字数 (BCrypt の上限を考慮)
     */
    private static final int MAX_LENGTH = 72;

    /**
     * 許可文字パターン (英数記号)
     */
    private static final String ALLOWED_PATTERN = "^[\\x21-\\x7E]+$";


    String value;

    /**
     * 生パスワードを指定して {@link RawPassword} インスタンスを生成します。
     *
     * @param value パスワード文字列
     * @return {@link RawPassword} インスタンス
     * @throws IllegalArgumentException バリデーションエラーの場合
     */
    public static RawPassword of(final String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Password must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters");
        }

        if (!value.matches(ALLOWED_PATTERN)) {
            throw new IllegalArgumentException("Password contains invalid characters. Only alphanumeric and symbols are allowed.");
        }

        return new RawPassword(value);
    }

    /**
     * ログ出力などでパスワードが露出するのを防ぐため、値をマスクします。
     */
    @ToString.Include
    public String value() {
        return "********";
    }

    /**
     * 実際のパスワード値を取得します。
     *
     * @return 生のパスワード文字列
     */
    public String rawValue() {
        return value;
    }
}