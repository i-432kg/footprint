package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.value.Coordinate;
import lombok.*;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class Location {


    @Nullable
    Coordinate latitude;

    @Nullable
    Coordinate longitude;

    public Optional<Coordinate> getLatitude() {
        return Optional.ofNullable(latitude);
    }

    public Optional<Coordinate> getLongitude() {
        return Optional.ofNullable(longitude);
    }

    /**
     * 位置情報が不明のオブジェクトを生成します
     */
    public static Location unknown() {
        return new Location(null, null);
    }

    /**
     * 位置情報が有効かどうかを判定します
     */
    public boolean hasLocation() {
        return latitude != null && longitude != null;
    }
}
