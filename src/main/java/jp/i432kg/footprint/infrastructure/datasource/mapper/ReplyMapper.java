package jp.i432kg.footprint.infrastructure.datasource.mapper;

import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.value.Comment;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.domain.value.UserId;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReplyMapper {

    @Select("SELECT id, post_id, user_id, content, created_at, child_count FROM replies WHERE post_id = #{post_id} AND parent_reply_id IS NULL")
    List<Reply> findByPostIdAndParentReplyIdIsNull(@Param("post_id") PostId postId);

    @Select("SELECT id, post_id, user_id, parent_reply_id, content, created_at, child_count FROM replies WHERE parent_reply_id = #{parent_reply_id}")
    List<Reply> findByParentReplyId(@Param("parent_reply_id") ReplyId parentReplyId);

    @Insert("INSERT INTO replies (post_id, user_id, parent_reply_id, content) VALUES (#{post_id}, #{user_id}, #{parent_reply_id}, #{content})")
    void insert(
            @Param("post_id") PostId postId,
            @Param("user_id") UserId userId,
            @Param("parent_reply_id") ReplyId parentReplyId,
            @Param("content") Comment content
    );

    @Update("UPDATE replies SET child_count = child_count + 1 WHERE id = #{id}")
    void incrementChildCount(@Param("id") ReplyId replyId);

    @Select("SELECT id, post_id, user_id, parent_reply_id, content, created_at, child_count FROM replies WHERE user_id = #{user_id}")
    List<Reply> findByUserId(@Param("user_id") UserId userId);
}
