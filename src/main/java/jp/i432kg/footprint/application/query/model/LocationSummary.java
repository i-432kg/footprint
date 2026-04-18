package jp.i432kg.footprint.application.query.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.jspecify.annotations.Nullable;

/**
 * 位置情報の参照専用モデル
 */
@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class LocationSummary {

    /**
     * 緯度
     */
    @Nullable
    Double lat;

    /**
     * 経度
     */
    @Nullable
    Double lng;
}
