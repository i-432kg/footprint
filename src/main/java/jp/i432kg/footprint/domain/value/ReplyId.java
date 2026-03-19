package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Objects;

/**
 * 返信を一意に識別するための ID を表す値オブジェクト
 * <p>
 * フロント側に公開する ID として扱う
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReplyId {

    String value;

    public static ReplyId of(final String value) {
        if (Objects.isNull(value)) {
            throw new InvalidValueException("commnon.invalid.null", "field.reply_id");
        }
        if (value.isBlank()) {
            throw new InvalidValueException("commnon.invalid.blank", "field.reply_id");
        }
        return new ReplyId(value);
    }

    public String value() {
        return value;
    }
}