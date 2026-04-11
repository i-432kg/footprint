package jp.i432kg.footprint.infrastructure.datasource.mapper.repository;

import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.model.ParentReply;
import jp.i432kg.footprint.domain.value.Comment;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 返信に関する Mybatis マッパーインターフェース
 */
@Mapper
public interface ReplyMapper {

    /**
     * 返信IDに基づいて返信を取得します。
     */
    Optional<ReplyResultEntity> findReplyById(@Param("replyId") ReplyId replyId);

    /**
     * 返信を新規登録します。
     */
    void insert(ReplyInsertEntity params);

    /**
     * 返信のカウント（子返信数）を1つ増やします。
     */
    void incrementChildCount(@Param("id") ReplyId replyId);

    /**
     * Reply 用の Insert パラメータ保持クラス
     */
    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    class ReplyInsertEntity {
        private Long id;
        private ReplyId replyId;
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
                    reply.getReplyId(),
                    reply.getPostId(),
                    reply.getUserId(),
                    reply.getParentReplyId(),
                    reply.getMessage(),
                    0,
                    reply.getCreatedAt(),
                    reply.getCreatedAt()
            );
        }
    }

    /**
     * Reply 取得用の result mapping クラス
     */
    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    class ReplyResultEntity {
        private final ReplyId replyId;
        private final PostId postId;
        private final UserId userId;
        private final @Nullable ReplyId parentReplyId;
        private final Comment message;
        private final LocalDateTime createdAt;

        public Reply toDomain() {
            final ParentReply parentReply =
                    parentReplyId == null ? ParentReply.root() : ParentReply.of(parentReplyId);
            return Reply.of(
                    replyId,
                    postId,
                    userId,
                    parentReply,
                    message,
                    createdAt
            );
        }
    }
}
