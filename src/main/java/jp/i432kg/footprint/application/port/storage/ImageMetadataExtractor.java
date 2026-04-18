package jp.i432kg.footprint.application.port.storage;

import com.drew.imaging.ImageProcessingException;
import jp.i432kg.footprint.application.command.model.ImageMetadata;
import jp.i432kg.footprint.domain.value.StorageObject;

import java.io.IOException;

/**
 * 保存済み画像からアプリケーションで利用するメタデータを抽出するためのポートです。
 * <p>
 * EXIF 解析や画像サイズ取得などの技術処理は infrastructure 実装に閉じ込め、
 * application は {@link ImageMetadata} として解析結果を受け取ります。
 */
public interface ImageMetadataExtractor {

    /**
     * 保存済み画像からメタデータを抽出します。
     *
     * @param storageObject 保存済み画像の保存先情報
     * @return 画像メタデータ
     * @throws ImageProcessingException 画像解析に失敗した場合
     * @throws IOException ストレージアクセスに失敗した場合
     */
    ImageMetadata extract(StorageObject storageObject) throws ImageProcessingException, IOException;
}
