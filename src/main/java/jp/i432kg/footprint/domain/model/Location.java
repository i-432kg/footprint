package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.value.Coordinate;
import lombok.*;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

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
    Coordinate latitude;

    /**
     * 経度
     */
    Coordinate longitude;

    /**
     * 位置情報ドメインモデルを生成します。
     *
     * @param latitude  緯度
     * @param longitude 経度
     * @return {@link Location} インスタンス
     */
    public static Location of(final Coordinate latitude, final Coordinate longitude) {
        return new Location(latitude, longitude);
    }

    /**
     * 位置情報が不明のオブジェクトを生成します。
     */
    public static Location unknown() {
        return Location.of(null, null);
    }
}
