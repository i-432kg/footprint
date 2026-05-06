package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.helper.CoordinateValidation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;

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
     */
    public static Latitude of(final @Nullable BigDecimal value) {
        return new Latitude(CoordinateValidation.validate(FIELD_NAME, value, MIN, MAX, SCALE));
    }
}
