package jp.i432kg.footprint.infrastructure.datasource.mapper;

import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.value.*;
import org.apache.ibatis.annotations.*;

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

    void insert(
            @Param("user_id") UserId userId,
            @Param("image_file_name") ImageFileName imageFileName,
            @Param("comment") Comment comment,
            @Param("latitude") Coordinate latitude,
            @Param("longitude") Coordinate longitude
    );
}
