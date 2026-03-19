package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.Objects;

/**
 * ユーザーの生年月日を表す値オブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BirthDate {

    LocalDate value;

    /**
     * 生年月日を指定して {@link BirthDate} インスタンスを生成します。
     *
     * @param value 生年月日
     * @return {@link BirthDate} インスタンス
     * @throws InvalidValueException バリデーションエラーの場合
     */
    public static BirthDate of(final LocalDate value) {

        // null 禁止
        if (Objects.isNull(value)) {
            throw new InvalidValueException("common.invalid.null", "field.birthdate");
        }

        // 未来日を不許可
        if (value.isAfter(LocalDate.now())) {
            throw new InvalidValueException("birthdate.invalid.future");
        }

        return new BirthDate(value);
    }

    public LocalDate value() {
        return value;
    }
}
