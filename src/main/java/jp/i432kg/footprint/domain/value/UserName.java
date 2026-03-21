package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * ユーザー名を表す値オブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserName {

    /**
     * 最小文字数：4文字
     */
    static int MIN_LENGTH = 4;

    /**
     * 最大文字数：15文字
     */
    static int MAX_LENGTH = 15;

    /**
     * 英数および記号のみを許可し、空白文字を含まないパターン
     * \x21-\x7E は ASCII の可視文字（スペースを除く）
     */
    static Pattern ALLOWED_PATTERN = Pattern.compile("^[\\x21-\\x7E]+$");

    static String FIELD_NAME = "username";

    String value;

    /**
     * ユーザー名を指定して {@link UserName} インスタンスを生成します。
     *
     * @param value ユーザー名
     * @return {@link UserName} インスタンス
     * @throws InvalidValueException バリデーションエラーの場合
     */
    public static UserName of(final String value) {

        // null 禁止
        if (Objects.isNull(value)) {
            throw InvalidValueException.required(FIELD_NAME);
        }

        // 空白・改行のトリム
        final String trimmed = value.strip();

        // 文字数チェック
        if (trimmed.length() < MIN_LENGTH || trimmed.length() > MAX_LENGTH) {
            throw InvalidValueException.outOfRange(FIELD_NAME, trimmed.length(), MIN_LENGTH, MAX_LENGTH);
        }

        // 英数記号のみ（空白・制御文字不可）のチェック
        if (!ALLOWED_PATTERN.matcher(trimmed).matches()) {
            throw InvalidValueException.invalidFormat(FIELD_NAME, trimmed, ALLOWED_PATTERN.pattern());
        }

        return new UserName(trimmed);
    }

    public String value() {
        return value;
    }
}