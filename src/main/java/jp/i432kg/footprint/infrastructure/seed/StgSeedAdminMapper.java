package jp.i432kg.footprint.infrastructure.seed;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface StgSeedAdminMapper {

    Optional<String> findUserIdByEmail(@Param("email") String email);

    Optional<String> findPostIdByCaption(@Param("caption") String caption);

    boolean existsReplyByPostIdAndMessage(@Param("postId") String postId, @Param("message") String message);

    List<String> findSeedImageObjectKeys();

    void deleteSeedReplies();

    void deleteSeedPostImages();

    void deleteSeedPosts();

    void deleteSeedUsers();
}
