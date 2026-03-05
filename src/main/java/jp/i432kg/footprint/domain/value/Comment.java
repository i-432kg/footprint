package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Objects;

/**
 * 投稿や返信のコメント（本文）を表す値オブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Comment {

    /**
     * 最大文字長：100文字
     */
    static final int MAX_LENGTH = 100;

    String value;

    /**
     * コメント本文を指定して {@link Comment} インスタンスを生成します。
     *
     * @param value コメント本文
     * @return {@link Comment} インスタンス
     * @throws IllegalArgumentException バリデーションエラーの場合
     */
    public static Comment of(final String value) {

        // null 禁止
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("Comment cannot be null");
        }

        // 空文字・空白・改行のみを不許可
        if (value.strip().isBlank()) {
            throw new IllegalArgumentException("Comment cannot be empty or blank");
        }

        // 制御文字のチェック
        // \n (U+000A) と \r (U+000D) を除く、C0制御文字 (U+0000-U+001F) と DEL (U+007F) を禁止
        if (value.matches(".*[\\x00-\\x09\\x0B\\x0C\\x0E-\\x1F\\x7F].*")) {
            throw new IllegalArgumentException("Comment contains invalid control characters");
        }

        // 上限文字数のチェック
        if (MAX_LENGTH < value.length()) {
            throw new IllegalArgumentException("Comment exceeds the limit of " + MAX_LENGTH + " characters");
        }

        return new Comment(value);
    }

    public String value() {
        return value;
    }
}