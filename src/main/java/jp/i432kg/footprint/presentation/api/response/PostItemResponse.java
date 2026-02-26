package jp.i432kg.footprint.presentation.api.response;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 投稿アイテムのレスポンス
 */
@Value
@AllArgsConstructor(staticName = "of")
public class PostItemResponse {

    /**
     * 投稿 ID
     */
    Integer id;

    /**
     * 投稿コメント
     */
    String caption;

    /**
     * 投稿画像
     */
    List<ImageResponse> images;

    /**
     * 位置情報があるかどうか
     */
    boolean hasLocation;

    /**
     * 位置情報
     */
    LocationResponse location;

    /**
     * 投稿作成日時
     * ISO 8601 形式の日時フォーマット
     */
    OffsetDateTime createdAt;
}
