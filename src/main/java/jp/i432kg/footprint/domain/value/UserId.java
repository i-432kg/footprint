package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * ユーザー ID を表す値オブジェクト。
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserId {

    int value;

    public static UserId of(final int value) {
        return new UserId(value);
    }

    public int value() {
        return value;
    }
}