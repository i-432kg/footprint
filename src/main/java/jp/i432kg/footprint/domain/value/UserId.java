package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;


/**
 * ユーザーを一意に識別するための ID を表す値オブジェクト
 * <p>
 * フロント側に公開する ID として扱う
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserId {

    String value;

    public static UserId of(final String value) {
        return new UserId(value);
    }

    public String value() {
        return value;
    }
}