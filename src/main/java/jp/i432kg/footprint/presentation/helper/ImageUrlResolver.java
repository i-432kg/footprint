package jp.i432kg.footprint.presentation.helper;

import jp.i432kg.footprint.domain.value.StorageObject;

/**
 * 保存先種別に応じて、画像表示用 URL を解決する presentation 向けポートです。
 */
public interface ImageUrlResolver {

    /**
     * 指定された保存オブジェクトから、ブラウザ表示用の URL を解決します。
     *
     * @param storageObject 保存先情報
     * @return 画像表示用 URL
     */
    String resolve(StorageObject storageObject);
}
