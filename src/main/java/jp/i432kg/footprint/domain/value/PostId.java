package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * 投稿 ID を表す値オブジェクト。
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostId {

    int value;

    /**
     * 数値を指定して {@link PostId} インスタンスを生成します。
     *
     * @param value 投稿 ID
     * @return {@link PostId} インスタンス
     */
    public static PostId of(int value) {
        return new PostId(value);
    }

    /**
     * 文字列を指定して {@link PostId} インスタンスを生成します。
     *
     * @param value 投稿 ID の文字列
     * @return {@link PostId} インスタンス
     */
    public static PostId valueOf(String value) {
        return new PostId(Integer.parseInt(value));
    }

    public int value() {
        return value;
    }
}