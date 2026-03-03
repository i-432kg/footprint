package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * 投稿や返信のコメント（本文）を表す値オブジェクト。
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Comment {

    String value;

    /**
     * コメント本文を指定して {@link Comment} インスタンスを生成します。
     *
     * @param value コメント本文
     * @return {@link Comment} インスタンス
     * @throws IllegalArgumentException 引数が null の場合
     */
    public static Comment of(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("Comment cannot be null");
        }
        return new Comment(value);
    }

    public String value() {
        return value;
    }
}