package jp.i432kg.footprint.application.query.model;

import lombok.NoArgsConstructor;
import lombok.Value;

/**
 * 投稿画像の参照専用モデル
 */
@Value
@NoArgsConstructor(force = true)
public class ImageSummary {

    /**
     * 画像 ID
     */
    Integer id;

    /**
     * 画像の並び順
     */
    Integer sortOrder;

    /**
     * 画像の URL
     */
    String url;

    /**
     * 画像の MIME タイプ
     */
    String contentType;

    /**
     * 画像のサイズ（Byte）
     */
    Long sizeBytes;

    /**
     * 画像の横幅
     */
    Integer width;

    /**
     * 画像の縦幅
     */
    Integer height;
}
