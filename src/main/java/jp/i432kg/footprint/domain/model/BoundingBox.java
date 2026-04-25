package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.exception.InvalidModelException;
import jp.i432kg.footprint.domain.value.Latitude;
import jp.i432kg.footprint.domain.value.Longitude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;

/**
 * 地図検索で利用する境界ボックスを表すドメインモデル。
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class BoundingBox {

    Latitude minLat;

    Latitude maxLat;

    Longitude minLng;

    Longitude maxLng;

    /**
     * 生の座標値から {@link BoundingBox} を生成する。
     *
     * @param rawMinLat 最小緯度
     * @param rawMaxLat 最大緯度
     * @param rawMinLng 最小経度
     * @param rawMaxLng 最大経度
     * @return {@link BoundingBox} インスタンス
     */
    public static BoundingBox of(
            final BigDecimal rawMinLat,
            final BigDecimal rawMaxLat,
            final BigDecimal rawMinLng,
            final BigDecimal rawMaxLng
    ) {
        return of(
                Latitude.of(rawMinLat),
                Latitude.of(rawMaxLat),
                Longitude.of(rawMinLng),
                Longitude.of(rawMaxLng)
        );
    }

    /**
     * 正規化済みの座標値から {@link BoundingBox} を生成する。
     *
     * @param minLat 最小緯度
     * @param maxLat 最大緯度
     * @param minLng 最小経度
     * @param maxLng 最大経度
     * @return {@link BoundingBox} インスタンス
     */
    public static BoundingBox of(
            final Latitude minLat,
            final Latitude maxLat,
            final Longitude minLng,
            final Longitude maxLng
    ) {
        if (minLat.getValue().compareTo(maxLat.getValue()) > 0) {
            throw InvalidModelException.invalid("boundingBox", minLat.getValue(), "min_lat_gt_max_lat");
        }

        if (minLng.getValue().compareTo(maxLng.getValue()) > 0) {
            throw InvalidModelException.invalid("boundingBox", minLng.getValue(), "min_lng_gt_max_lng");
        }

        return new BoundingBox(minLat, maxLat, minLng, maxLng);
    }
}
