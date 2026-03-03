package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * 緯度・経度の座標値を表す値オブジェクト。
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Coordinate {

    double value;

    /**
     * 座標値を指定して {@link Coordinate} インスタンスを生成します。
     *
     * @param value 座標値
     * @return {@link Coordinate} インスタンス
     */
    public static Coordinate of(final double value) {
        return new Coordinate(value);
    }

    public double value() {
        return value;
    }
}