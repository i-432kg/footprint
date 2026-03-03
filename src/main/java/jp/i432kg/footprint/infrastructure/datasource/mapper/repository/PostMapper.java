package jp.i432kg.footprint.infrastructure.datasource.mapper.repository;

import jp.i432kg.footprint.domain.model.Image;
import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.value.*;
import jp.i432kg.footprint.domain.value.Byte;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

/**
 * 投稿に関する Mybatis マッパーインターフェース
 */
@Mapper
public interface PostMapper {

    void insertPosts(PostInsertEntity params);

    /**
     * PostImages テーブルに新しいレコードを挿入します。
     *
     * @param params 挿入するデータを保持する {@link PostImageInsertEntity} オブジェクト
     */
    void insertPostImages(PostImageInsertEntity params);

    /**
     * Insert 用のパラメータ保持クラス
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    class PostInsertEntity {
        private Long id; // MyBatis が書き込むため final にしない
        private final UserId userId;
        private final Comment caption;
        private final boolean hasLocation;
        private final Coordinate latitude;
        private final Coordinate longitude;
        private final LocalDateTime takenAt;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        public static PostInsertEntity from(final Post post) {
            return new PostInsertEntity(
                    null, // Insert 前なので ID は null
                    post.getUserId(),
                    post.getCaption(),
                    post.getImage().hasLocation(),
                    post.getImage().getLocation().getLatitude(),
                    post.getImage().getLocation().getLongitude(),
                    post.getImage().getTakenAt(),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
        }
    }

    /**
     * Insert 用のパラメータ保持クラス
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    class PostImageInsertEntity {
        private Long id; // MyBatis が書き込むため final にしない
        private final PostId postId;
        private final int sortOrder;
        private final StorageType storageType;
        private final FilePath path;
        private final String contentType;
        private final Byte sizeBytes;
        private final Pixel width;
        private final Pixel height;
        private final boolean exifAvailable;
        private final LocalDateTime createdAt;

        public static PostImageInsertEntity from(final PostId postId, final Post post) {

            final Image image = post.getImage();

            return new PostImageInsertEntity(
                    null, // Insert 前なので ID は null
                    postId,
                    0,
                    StorageType.LOCAL,
                    image.getFilePath(),
                    image.getContentType(),
                    image.getFileSize(),
                    image.getWidth(),
                    image.getHeight(),
                    image.isHasEXIF(),
                    LocalDateTime.now()
            );
        }
    }
}
