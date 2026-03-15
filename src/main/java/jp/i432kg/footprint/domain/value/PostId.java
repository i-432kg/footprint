package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * 投稿を一意に識別するための ID を表す値オブジェクト
 * <p>
 * フロント側に公開する ID として扱う
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostId {

    String value;

    /**
     * 数値を指定して {@link PostId} インスタンスを生成します。
     *
     * @param value 投稿 ID
     * @return {@link PostId} インスタンス
     */
    public static PostId of(String value) {
        return new PostId(value);
    }

    public String value() {
        return value;
    }
}