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
 * 投稿永続化用の MyBatis マッパーインターフェース。
 */
@Mapper
public interface PostMapper {

    /**
     * 指定した投稿 ID の登録件数を取得する。
     *
     * @param postId 投稿 ID
     * @return 検索にヒットした件数
     */
    int countByPostId(@Param("postId") PostId postId);

    /**
     * 投稿レコードを登録する。
     *
     * @param params 登録する投稿パラメータ
     */
    void insertPosts(PostInsertEntity params);

    /**
     * 投稿画像レコードを登録する。
     *
     * @param params 登録する投稿画像パラメータ
     */
    void insertPostImages(PostImageInsertEntity params);

    /** 投稿 insert 用のパラメータ。 */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    class PostInsertEntity {
        private Long id; // MyBatis が書き込むため final にしない
        private final PostId postId;
        private final UserId userId;
        private final PostComment caption;
        private final boolean hasLocation;
        private final Latitude latitude;
        private final Longitude longitude;
        private final LocalDateTime takenAt;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        public static PostInsertEntity from(final Post post) {
            return new PostInsertEntity(
                    null, // Insert 前なので ID は null
                    post.getPostId(),
                    post.getUserId(),
                    post.getCaption(),
                    post.getImage().hasLocation(),
                    post.getImage().getLocation().getLatitude(),
                    post.getImage().getLocation().getLongitude(),
                    post.getImage().getTakenAt(),
                    post.getCreatedAt(),
                    post.getCreatedAt()
            );
        }
    }

    /** 投稿画像 insert 用のパラメータ。 */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    class PostImageInsertEntity {
        private Long id; // MyBatis が書き込むため final にしない
        private final PostId postId;
        private final int sortOrder;
        private final StorageType storageType;
        private final ObjectKey objectKey;
        private final FileExtension fileExtension;
        private final Byte sizeBytes;
        private final Pixel width;
        private final Pixel height;
        private final boolean exifAvailable;
        private final LocalDateTime createdAt;

        public static PostImageInsertEntity from(final Post post) {

            final Image image = post.getImage();

            return new PostImageInsertEntity(
                    null, // Insert 前なので ID は null
                    post.getPostId(),
                    0,
                    image.getStorageObject().getStorageType(),
                    image.getStorageObject().getObjectKey(),
                    image.getFileExtension(),
                    image.getFileSize(),
                    image.getWidth(),
                    image.getHeight(),
                    image.isHasEXIF(),
                    post.getCreatedAt()
            );
        }
    }
}
