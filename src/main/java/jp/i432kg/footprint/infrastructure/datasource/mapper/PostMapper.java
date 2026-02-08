package jp.i432kg.footprint.infrastructure.datasource.mapper;

import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.value.*;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PostMapper {

    @Select("SELECT id, user_id, image_file_name, comment, latitude, longitude, created_at FROM posts WHERE id = #{post_id}")
    @Results(id = "postResult", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "imageFileName", column = "image_file_name"),
            @Result(property = "comment", column = "comment"),
            @Result(property = "location.latitude", column = "latitude"),
            @Result(property = "location.longitude", column = "longitude"),
            @Result(property = "createdAt", column = "created_at")
    })
    Optional<Post> findPostByPostId(@Param("post_id") PostId postId);

    @Select("SELECT id, user_id, image_file_name, comment, latitude, longitude, created_at FROM posts ORDER BY created_at DESC LIMIT 10")
    @ResultMap("postResult")
    List<Post> findRecentPosts();

    @Insert("INSERT INTO posts (user_id, image_file_name, comment, latitude, longitude) VALUES (#{user_id}, #{image_file_name}, #{comment}, #{latitude}, #{longitude})")
    void insert(
            @Param("user_id") UserId userId,
            @Param("image_file_name") ImageFileName imageFileName,
            @Param("comment") Comment comment,
            @Param("latitude") Coordinate latitude,
            @Param("longitude") Coordinate longitude
    );

    List<Post> searchPosts(
            @Param("keyword") SearchKeyword keyword,
            @Param("lastId") PostId lastId,
            @Param("size") int size
    );

    @Select("SELECT id, user_id, image_file_name, comment, latitude, longitude, created_at FROM posts WHERE user_id = #{user_id}")
    @ResultMap("postResult")
    List<Post> findPostByUserId(@Param("user_id") UserId userId);
}
