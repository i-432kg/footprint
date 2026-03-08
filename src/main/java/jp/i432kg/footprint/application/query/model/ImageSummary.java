package jp.i432kg.footprint.application.query.model;

import jp.i432kg.footprint.domain.value.ObjectKey;
import jp.i432kg.footprint.domain.value.StorageType;
import lombok.Value;
import lombok.NoArgsConstructor;

/**
 * 画像の参照専用モデル
 */
@Value
@NoArgsConstructor(force = true)
public class ImageSummary {

    /**
     * 画像 ID
     */
    Integer id;

    /**
     * 表示順
     */
    Integer sortOrder;

    /**
     * 保存種別
     */
    StorageType storageType;

    /**
     * 保存先オブジェクトキー
     */
    ObjectKey objectKey;

    /**
     * MIME タイプ
     */
    String contentType;

    /**
     * ファイルサイズ
     */
    Long sizeBytes;

    /**
     * 横幅
     */
    Integer width;

    /**
     * 高さ
     */
    Integer height;
}