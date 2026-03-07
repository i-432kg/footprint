package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.value.Latitude;
import jp.i432kg.footprint.domain.value.Longitude;
import lombok.*;

/**
 * 位置情報ドメインモデル
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class Location {

    /**
     * 緯度
     */
    Latitude latitude;

    /**
     * 経度
     */
    Longitude longitude;

    /**
     * 位置情報ドメインモデルを生成します。
     *
     * @param latitude  緯度
     * @param longitude 経度
     * @return {@link Location} インスタンス
     */
    public static Location of(final Latitude latitude, final Longitude longitude) {
        return new Location(latitude, longitude);
    }

    /**
     * 位置情報が不明のオブジェクトを生成します。
     */
    public static Location unknown() {
        return Location.of(null, null);
    }
}
