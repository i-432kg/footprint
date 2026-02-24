package jp.i432kg.footprint.infrastructure.datasource.mapper;

import jp.i432kg.footprint.domain.value.ImageFileName;
import jp.i432kg.footprint.domain.value.PostId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.ibatis.annotations.Mapper;

/**
 * PostImages テーブルへの操作を行う Mapper インターフェース
 */
@Mapper
public interface PostImagesMapper {

    /**
     * PostImages テーブルに新しいレコードを挿入します。
     *
     * @param params 挿入するデータを保持する {@link PostImageInsertEntity} オブジェクト
     */
    void insert(PostImageInsertEntity params);

    /**
     * Insert 用のパラメータ保持クラス
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    class PostImageInsertEntity {
        private Long id; // MyBatis が書き込むため final にしない
        private final PostId postId;
        private final int sortOrder;
        private final String storageType;
        private final String path;
        private final String contentType;
        private final long sizeBytes;
        private final boolean exifAvailable;

        public static PostImageInsertEntity from(PostId postId, ImageFileName fileName, boolean exifAvailable) {
            // TODO ドメインモデルの整理
            return new PostImageInsertEntity(
                    null,
                    postId,
                    0,
                    "LOCAL",
                    fileName.value(),
                    "image/jpeg",
                    0L,
                    exifAvailable
            );
        }
    }
}