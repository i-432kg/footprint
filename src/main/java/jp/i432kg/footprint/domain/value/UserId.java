package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
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
        if (value == null) {
            throw new InvalidValueException("common.invalid.null", "field.user_id");
        }
        if (value.isBlank()) {
            throw new InvalidValueException("common.invalid.blank", "field.user_id");
        }
        return new UserId(value);
    }

    public String value() {
        return value;
    }
}