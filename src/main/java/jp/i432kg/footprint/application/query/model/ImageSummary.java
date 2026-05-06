package jp.i432kg.footprint.application.query.model;

import jp.i432kg.footprint.domain.value.ObjectKey;
import jp.i432kg.footprint.domain.value.StorageType;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.NoArgsConstructor;

/**
 * 画像の参照専用モデル
 */
@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ImageSummary {

    /**
     * 画像 ID
     */
    String id;

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
     * 画像の拡張子
     */
    String fileExtension;

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
