package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Value;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Pattern;

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
    static int MIN_LENGTH = 8;

    /**
     * 最大文字数 (BCrypt の上限を考慮)
     */
    static int MAX_LENGTH = 72;

    /**
     * 許可文字パターン (英数記号)
     */
    static Pattern ALLOWED_PATTERN = Pattern.compile("^[\\x21-\\x7E]+$");

    static String FIELD_NAME = "password";

    String value;

    /**
     * 生パスワードを指定して {@link RawPassword} インスタンスを生成します。
     *
     * @param value パスワード文字列
     * @return {@link RawPassword} インスタンス
     * @throws InvalidValueException バリデーションエラーの場合
     */
    public static RawPassword of(final @Nullable String value) {

        // null 禁止
        if (Objects.isNull(value)) {
            throw InvalidValueException.required(FIELD_NAME);
        }

        // 空文字のみを不許可
        if (value.isBlank()) {
            throw InvalidValueException.blank(FIELD_NAME);
        }

        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw InvalidValueException.outOfRange(FIELD_NAME, value.length(), MIN_LENGTH, MAX_LENGTH);
        }

        if (!ALLOWED_PATTERN.matcher(value).matches()) {
            throw InvalidValueException.invalidFormat(FIELD_NAME, value, ALLOWED_PATTERN.pattern());
        }

        return new RawPassword(value);
    }

    /**
     * ログ出力などでパスワードが露出するのを防ぐため、値をマスクします。
     */
    @ToString.Include
    public String maskedValue() {
        return "********";
    }

    /**
     * 実際のパスワード値を取得します。
     *
     * @return 生のパスワード文字列
     */
    public String value() {
        return value;
    }
}
