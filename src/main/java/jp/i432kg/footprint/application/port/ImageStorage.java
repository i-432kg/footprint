package jp.i432kg.footprint.application.port;

import jp.i432kg.footprint.domain.value.FileName;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.StorageObject;
import jp.i432kg.footprint.domain.value.UserId;

import java.io.IOException;
import java.io.InputStream;

/**
 * 画像ファイルを物理ストレージへ保存するためのアプリケーションポートです。
 * <p>
 * 保存先がローカルファイルシステムか S3 かといった技術詳細は infrastructure 実装に閉じ込め、
 * application は保存結果として {@link StorageObject} だけを受け取ります。
 */
public interface ImageStorage {

    /**
     * 画像ファイルを保存します。
     *
     * @param imageStream 画像データ
     * @param originalFilename 元のファイル名
     * @param userId 投稿者 ID
     * @param postId 投稿 ID
     * @return 保存後のストレージ情報
     * @throws IOException 保存に失敗した場合
     */
    StorageObject store(
            InputStream imageStream,
            FileName originalFilename,
            UserId userId,
            PostId postId
    ) throws IOException;

    /**
     * 保存済み画像を物理ストレージから削除します。
     *
     * @param storageObject 削除対象の保存先情報
     * @throws IOException 削除に失敗した場合
     */
    void delete(StorageObject storageObject) throws IOException;
}
