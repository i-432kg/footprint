package jp.i432kg.footprint.infrastructure.datasource.mapper.repository;

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

/**
 * 返信に関する Mybatis マッパーインターフェース
 */
@Mapper
public interface ReplyMapper {

    @Insert("""
            INSERT INTO replies (post_id, user_id, parent_reply_id, message, child_count, created_at, updated_at)
            VALUES (#{postId}, #{userId}, #{parentReplyId}, #{message}, #{childCount}, #{createdAt}, #{updatedAt})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ReplyInsertEntity params);

    @Update("UPDATE replies SET child_count = child_count + 1 WHERE id = #{id}")
    void incrementChildCount(@Param("id") ReplyId replyId);

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
        private final Comment message;
        private final int childCount;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        public static ReplyInsertEntity from(final Reply reply) {
            return new ReplyInsertEntity(
                    null, // insert 前なので null
                    reply.getPostId(),
                    reply.getUserId(),
                    reply.getParentReplyId(),
                    reply.getMessage(),
                    0,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
        }
    }
}
