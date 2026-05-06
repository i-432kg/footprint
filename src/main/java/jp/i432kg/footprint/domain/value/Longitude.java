package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.helper.CoordinateValidation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;

/**
 * 経度を表す値オブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Longitude {

    /**
     * 経度の有効範囲下限：-180度
     */
    static BigDecimal MIN = new BigDecimal("-180");

    /**
     * 経度の有効範囲上限：180度
     */
    static BigDecimal MAX = new BigDecimal("180");

    /**
     * 小数点以下の有効桁数：6桁
     */
    static int SCALE = 6;

    static String FIELD_NAME = "longitude";

    BigDecimal value;

    /**
     * 経度を指定して {@link Longitude} インスタンスを生成します。
     *
     * @param value 経度
     * @return {@link Longitude} インスタンス
     */
    public static Longitude of(final @Nullable BigDecimal value) {
        return new Longitude(CoordinateValidation.validate(FIELD_NAME, value, MIN, MAX, SCALE));
    }
}
