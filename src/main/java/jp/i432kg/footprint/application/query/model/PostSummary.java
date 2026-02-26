package jp.i432kg.footprint.application.query.model;

import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 投稿の参照専用モデル
 */
@Value
@NoArgsConstructor(force = true)
public class PostSummary {

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
    List<ImageSummary> images;

    /**
     * 位置情報があるかどうか
     */
    boolean hasLocation;

    /**
     * 位置情報
     */
    LocationSummary location;

    /**
     * 投稿作成日時
     */
    LocalDateTime createdAt;
}
