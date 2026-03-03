package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

/**
 * ユーザーの生年月日を表す値オブジェクト。
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
     * @throws IllegalArgumentException 引数が null の場合
     */
    public static BirthDate of(final LocalDate value) {
        if (value == null) {
            throw new IllegalArgumentException("BirthDate cannot be null");
        }
        return new BirthDate(value);
    }

    public LocalDate value() {
        return value;
    }
}
