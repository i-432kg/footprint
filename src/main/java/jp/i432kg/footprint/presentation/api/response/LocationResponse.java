package jp.i432kg.footprint.presentation.api.response;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * 投稿の位置情報のレスポンス
 */
@Value
@AllArgsConstructor(staticName = "of")
public class LocationResponse {

    /**
     * 緯度
     */
    Double lat;

    /**
     * 経度
     */
    Double lng;
}
