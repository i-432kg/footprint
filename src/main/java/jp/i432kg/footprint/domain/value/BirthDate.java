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
     * <p>
     * 入力値の妥当性検証として、基準日 {@code today} より未来の日付を拒否します。
     *
     * @param value 生年月日
     * @param today 未来日判定に用いる基準日
     * @return {@link BirthDate} インスタンス
     * @throws InvalidValueException バリデーションエラーの場合
     */
    public static BirthDate of(final @Nullable LocalDate value, final LocalDate today) {

        // null 禁止
        if (Objects.isNull(value)) {
            throw InvalidValueException.required(FIELD_NAME);
        }

        // 未来日を不許可
        if (value.isAfter(today)) {
            throw InvalidValueException.invalid(FIELD_NAME, value, "must not be in the future");
        }

        return new BirthDate(value);
    }

    /**
     * 永続化済みデータなどから {@link BirthDate} を再構築します。
     * <p>
     * 既に妥当性確認済みの値を復元する用途を想定し、未来日チェックは行いません。
     *
     * @param value 生年月日
     * @return {@link BirthDate} インスタンス
     * @throws InvalidValueException 値が null の場合
     */
    public static BirthDate restore(final @Nullable LocalDate value) {
        if (Objects.isNull(value)) {
            throw InvalidValueException.required(FIELD_NAME);
        }

        return new BirthDate(value);
    }
}
