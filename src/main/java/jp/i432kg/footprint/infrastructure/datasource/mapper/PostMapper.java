package jp.i432kg.footprint.infrastructure.datasource.mapper;

import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.value.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface PostMapper {

    Optional<Post> findPostByPostId(@Param("post_id") PostId postId);

    List<Post> findPostByUserId(@Param("user_id") UserId userId);

    List<Post> findRecentPosts(
            @Param("lastId") PostId lastId,
            @Param("size") int size
    );

    List<Post> searchPosts(
            @Param("keyword") SearchKeyword keyword,
            @Param("lastId") PostId lastId,
            @Param("size") int size
    );

    void insert(PostInsertEntity params);

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

        public static PostInsertEntity from(final Post.NewPost newPost) {
            return new PostInsertEntity(
                    null, // Insert 前なので ID は null
                    newPost.getUserId(),
                    newPost.getComment(),
                    newPost.getLocation().hasLocation(),
                    newPost.getLocation().getLatitude().orElse(null),
                    newPost.getLocation().getLongitude().orElse(null),
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
        }
    }
}
