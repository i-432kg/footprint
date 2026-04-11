package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.util.Objects;

/**
 * ユーザーの生年月日を表す値オブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BirthDate {

    static String FIELD_NAME = "birthdate";

    LocalDate value;

    /**
     * 生年月日を指定して {@link BirthDate} インスタンスを生成します。
     *
     * @param value 生年月日
     * @return {@link BirthDate} インスタンス
     * @throws InvalidValueException バリデーションエラーの場合
     */
    public static BirthDate of(final @Nullable LocalDate value) {

        // null 禁止
        if (Objects.isNull(value)) {
            throw InvalidValueException.required(FIELD_NAME);
        }

        // 未来日を不許可
        if (value.isAfter(LocalDate.now())) {
            throw InvalidValueException.invalid(FIELD_NAME, value, "must not be in the future");
        }

        return new BirthDate(value);
    }
}
