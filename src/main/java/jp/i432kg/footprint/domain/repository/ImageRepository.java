package jp.i432kg.footprint.domain.repository;

import jp.i432kg.footprint.domain.model.Image;
import jp.i432kg.footprint.domain.value.FileName;
import jp.i432kg.footprint.domain.value.FilePath;

import java.io.InputStream;


/**
 * 画像に関するリポジトリインターフェース
 */
public interface ImageRepository {

    /**
     * 保存済みの画像ファイルからドメインモデルに必要なメタ情報を一括抽出します。
     */
    Image extractImageMetadata(FilePath filePath);

    /**
     * 画像をストレージに保存し、保存後のファイル名を返します。
     */
    FileName save(InputStream imageStream, FileName originalFilename);

}
