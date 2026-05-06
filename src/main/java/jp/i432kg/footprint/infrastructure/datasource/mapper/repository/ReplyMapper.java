package jp.i432kg.footprint.infrastructure.datasource.mapper.repository;

import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.model.ParentReply;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.domain.value.ReplyComment;
import jp.i432kg.footprint.domain.value.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 返信永続化用の MyBatis マッパーインターフェース。
 */
@Mapper
public interface ReplyMapper {

    /**
     * 返信 ID に基づいて返信を取得する。
     *
     * @param replyId 返信 ID
     * @return 返信取得結果
     */
    Optional<ReplyResultEntity> findReplyById(@Param("replyId") ReplyId replyId);

    /**
     * 返信レコードを登録する。
     *
     * @param params 登録する返信パラメータ
     */
    void insert(ReplyInsertEntity params);

    /**
     * 子返信数を 1 件増やす。
     *
     * @param replyId 親返信 ID
     */
    void incrementChildCount(@Param("id") ReplyId replyId);

    /** 返信 insert 用のパラメータ。 */
    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    class ReplyInsertEntity {
        private Long id;
        private ReplyId replyId;
        private final PostId postId;
        private final UserId userId;
        private final ReplyId parentReplyId;
        private final ReplyComment message;
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

    /** 返信取得結果を domain model へ変換するためのデータ。 */
    @Getter
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    class ReplyResultEntity {
        private final ReplyId replyId;
        private final PostId postId;
        private final UserId userId;
        private final @Nullable ReplyId parentReplyId;
        private final ReplyComment message;
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
