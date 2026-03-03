package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * 返信 ID を表す値オブジェクト。
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReplyId {

    int value;

    public static ReplyId of(final int value) {
        return new ReplyId(value);
    }

    public int value() {
        return value;
    }
}