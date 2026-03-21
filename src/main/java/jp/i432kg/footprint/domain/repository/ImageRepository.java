package jp.i432kg.footprint.domain.repository;

import com.drew.imaging.ImageProcessingException;
import jp.i432kg.footprint.domain.model.Image;
import jp.i432kg.footprint.domain.value.FileName;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.StorageObject;
import jp.i432kg.footprint.domain.value.UserId;

import java.io.IOException;
import java.io.InputStream;


/**
 * 画像に関するリポジトリインターフェース
 */
public interface ImageRepository {

    /**
     * 保存済みの画像ファイルからドメインモデルに必要なメタ情報を一括抽出します。
     */
    Image extractImageMetadata(StorageObject storageObject) throws ImageProcessingException, IOException;

    /**
     * 画像をストレージに保存し、保存後の保存先情報を返します。
     *
     * @param imageStream      画像データ
     * @param originalFilename 元のファイル名
     * @param userId           投稿者の ID
     * @param postId           投稿の ID
     * @return 生成されたストレージオブジェクト
     */
    StorageObject save(
            InputStream imageStream,
            FileName originalFilename,
            UserId userId,
            PostId postId
    ) throws IOException;

}
