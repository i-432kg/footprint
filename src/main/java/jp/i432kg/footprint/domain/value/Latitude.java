package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 緯度を表す値オブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Latitude {

    /**
     * 緯度の有効範囲下限：-90度
     */
    static BigDecimal MIN = new BigDecimal("-90");

    /**
     * 緯度の有効範囲上限：90度
     */
    static BigDecimal MAX = new BigDecimal("90");

    /**
     * 小数点以下の有効桁数：6桁
     */
    static int SCALE = 6;

    static String FIELD_NAME = "latitude";

    BigDecimal value;

    /**
     * 緯度を指定して {@link Latitude} インスタンスを生成します。
     *
     * @param value 緯度
     * @return {@link Latitude} インスタンス
     * @throws InvalidValueException バリデーションエラーの場合
     */
    public static Latitude of(final @Nullable BigDecimal value) {

        // null 禁止
        if (Objects.isNull(value)) {
            throw InvalidValueException.required(FIELD_NAME);
        }

        // 小数第6位で四捨五入
        final BigDecimal roundedValue = value.setScale(SCALE, RoundingMode.HALF_UP);

        // 範囲内のチェック
        if (value.compareTo(MIN) < 0 || 0 < value.compareTo(MAX)) {
            throw InvalidValueException.outOfRange(FIELD_NAME, roundedValue, MIN, MAX);
        }

        return new Latitude(roundedValue);
    }
}
