package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.value.Latitude;
import jp.i432kg.footprint.domain.value.Longitude;
import lombok.*;
import org.jspecify.annotations.Nullable;

/**
 * 位置情報ドメインモデル
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class Location {

    private static final Location UNKNOWN = new Location(null, null, true);

    /**
     * 緯度
     */
    @Nullable
    Latitude latitude;

    /**
     * 経度
     */
    @Nullable
    Longitude longitude;

    /**
     * 位置情報が不明かどうか
     */
    boolean unknown;

    /**
     * 位置情報ドメインモデルを生成します。
     *
     * @param latitude  緯度
     * @param longitude 経度
     * @return {@link Location} インスタンス
     */
    public static Location of(final Latitude latitude, final Longitude longitude) {
        return new Location(latitude, longitude, false);
    }

    /**
     * 位置情報が不明のオブジェクトを生成します。
     */
    public static Location unknown() {
        return UNKNOWN;
    }
}
