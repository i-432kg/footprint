package jp.i432kg.footprint.infrastructure.seed.stg;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface StgSeedAdminMapper {

    Optional<String> findUserIdByEmail(@Param("email") String email);

    Optional<String> findPostIdByCaption(@Param("caption") String caption);

    Optional<String> findReplyIdByPostIdAndMessage(@Param("postId") String postId, @Param("message") String message);

    boolean existsReplyByPostIdAndMessage(@Param("postId") String postId, @Param("message") String message);

    List<String> findSeedImageObjectKeys();

    void deleteSeedReplies();

    void deleteSeedPostImages();

    void deleteSeedPosts();

    void deleteSeedUsers();
}
