package jp.i432kg.footprint.presentation.api.response;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 投稿一覧および投稿詳細で返却する投稿レスポンス DTO です。
 */
@Value
@AllArgsConstructor(staticName = "of")
public class PostItemResponse {

    /**
     * 投稿 ID です。
     */
    String id;

    /**
     * 投稿コメントです。
     */
    String caption;

    /**
     * 投稿に紐づく画像一覧です。
     */
    List<ImageResponse> images;

    /**
     * 位置情報が含まれているかどうかを表します。
     */
    boolean hasLocation;

    /**
     * 位置情報です。位置情報がない投稿では `null` になりえます。
     */
    LocationResponse location;

    /**
     * 投稿作成日時です。ISO 8601 形式で返却します。
     */
    OffsetDateTime createdAt;
}
