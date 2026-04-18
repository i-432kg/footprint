package jp.i432kg.footprint.presentation.api.response;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.jspecify.annotations.Nullable;

/**
 * 投稿に紐づく位置情報を表す API レスポンス DTO です。
 */
@Value
@AllArgsConstructor(staticName = "of")
public class LocationResponse {

    /**
     * 緯度です。
     */
    @Nullable
    Double lat;

    /**
     * 経度です。
     */
    @Nullable
    Double lng;
}
