package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * 投稿コメントを表す値オブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostComment {

    /**
     * 最大文字長：100文字
     */
    static int MAX_LENGTH = 100;

    /**
     * 許可文字パターン：
     * \n (U+000A) と \r (U+000D) を除く、C0制御文字 (U+0000-U+001F) と DEL (U+007F) を禁止する。
     */
    static String DISALLOWED_PATTERN = ".*[\\x00-\\x09\\x0B\\x0C\\x0E-\\x1F\\x7F].*";

    static String FIELD_NAME = "postComment";

    String value;

    /**
     * 投稿コメントを指定して {@link PostComment} インスタンスを生成します。
     *
     * @param value 投稿コメント
     * @return {@link PostComment} インスタンス
     * @throws InvalidValueException バリデーションエラーの場合
     */
    public static PostComment of(final @Nullable String value) {

        if (Objects.isNull(value)) {
            throw InvalidValueException.required(FIELD_NAME);
        }

        if (value.matches(DISALLOWED_PATTERN)) {
            throw InvalidValueException.invalidFormat(FIELD_NAME, value, DISALLOWED_PATTERN);
        }

        if (MAX_LENGTH < value.length()) {
            throw InvalidValueException.tooLong(FIELD_NAME, value, MAX_LENGTH);
        }

        return new PostComment(value);
    }
}
