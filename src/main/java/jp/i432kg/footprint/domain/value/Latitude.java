package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.jspecify.annotations.NonNull;

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
    private static final BigDecimal MIN = new BigDecimal("-90");

    /**
     * 緯度の有効範囲上限：90度
     */
    private static final BigDecimal MAX = new BigDecimal("90");

    /**
     * 小数点以下の有効桁数：6桁
     */
    private static final int SCALE = 6;

    BigDecimal value;

    /**
     * 緯度を指定して {@link Latitude} インスタンスを生成します。
     *
     * @param value 緯度
     * @return {@link Latitude} インスタンス
     * @throws IllegalArgumentException バリデーションエラーの場合
     */
    public static Latitude of(final BigDecimal value) {

        // null 禁止
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("Latitude cannot be null");
        }

        // 小数第6位で四捨五入
        final BigDecimal roundedValue = value.setScale(SCALE, RoundingMode.HALF_UP);

        // 範囲内のチェック
        if (value.compareTo(MIN) < 0 || 0 < value.compareTo(MAX)) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90: " + value);
        }

        return new Latitude(roundedValue);
    }

    public BigDecimal value() {
        return value;
    }
}
