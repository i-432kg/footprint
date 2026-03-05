package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 経度を表す値オブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Longitude {

    /**
     * 経度の有効範囲下限：-180度
     */
    private static final BigDecimal MIN = new BigDecimal("-180");

    /**
     * 経度の有効範囲上限：180度
     */
    private static final BigDecimal MAX = new BigDecimal("180");

    /**
     * 小数点以下の有効桁数：6桁
     */
    private static final int SCALE = 6;

    BigDecimal value;

    /**
     * 経度を指定して {@link Longitude} インスタンスを生成します。
     *
     * @param value 経度
     * @return {@link Longitude} インスタンス
     * @throws IllegalArgumentException バリデーションエラーの場合
     */
    public static Longitude of(final BigDecimal value) {

        // null 禁止
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("Longitude cannot be null");
        }

        // 小数第6位で四捨五入
        final BigDecimal roundedValue = value.setScale(SCALE, RoundingMode.HALF_UP);

        // 範囲内のチェック
        if (value.compareTo(MIN) < 0 || 0 < value.compareTo(MAX)) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180: " + value);
        }

        return new Longitude(roundedValue);
    }

    public BigDecimal value() {
        return value;
    }
}
