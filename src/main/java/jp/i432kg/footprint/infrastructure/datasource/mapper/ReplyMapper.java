package jp.i432kg.footprint.infrastructure.datasource.mapper;

import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.value.Comment;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReplyMapper {

    @Select("SELECT id, post_id, user_id, parent_reply_id, body AS content, child_count, created_at, updated_at FROM replies WHERE post_id = #{post_id} AND parent_reply_id IS NULL")
    List<Reply> findByPostIdAndParentReplyIdIsNull(@Param("post_id") PostId postId);

    @Select("SELECT id, post_id, user_id, parent_reply_id, body AS content, child_count, created_at, updated_at FROM replies WHERE parent_reply_id = #{parent_reply_id}")
    List<Reply> findByParentReplyId(@Param("parent_reply_id") ReplyId parentReplyId);

    @Insert("""
        INSERT INTO replies (post_id, user_id, parent_reply_id, body, child_count, created_at, updated_at)
        VALUES (#{postId}, #{userId}, #{parentReplyId}, #{body}, #{childCount}, #{createdAt}, #{updatedAt})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ReplyInsertEntity params);

    @Update("UPDATE replies SET child_count = child_count + 1 WHERE id = #{id}")
    void incrementChildCount(@Param("id") ReplyId replyId);

    @Select("SELECT id, post_id, user_id, parent_reply_id, body AS content, child_count, created_at, updated_at FROM replies WHERE user_id = #{user_id}")
    List<Reply> findByUserId(@Param("user_id") UserId userId);

    /**
     * Reply 用の Insert パラメータ保持クラス
     */
    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    class ReplyInsertEntity {
        private Long id;
        private final PostId postId;
        private final UserId userId;
        private final ReplyId parentReplyId;
        private final Comment body;
        private final int childCount;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        public static ReplyInsertEntity from(final Reply.NewReply newReply) {
            return new ReplyInsertEntity(
                    null, // insert 前なので null
                    newReply.getPostId(),
                    newReply.getUserId(),
                    newReply.getParentReplyId(),
                    newReply.getContent(),
                    0,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
        }
    }
}
