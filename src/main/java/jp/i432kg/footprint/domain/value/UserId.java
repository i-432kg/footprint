package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.helper.UlidValidation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.jspecify.annotations.Nullable;

/**
 * ユーザーを一意に識別するための ID を表す値オブジェクト
 * <p>
 * フロント側に公開する ID として扱う
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserId {

    static String FIELD_NAME = "user_id";

    String value;

    public static UserId of(final @Nullable String value) {
        final String validated = UlidValidation.requireValidUlid(FIELD_NAME, value);
        return new UserId(validated);
    }
}
