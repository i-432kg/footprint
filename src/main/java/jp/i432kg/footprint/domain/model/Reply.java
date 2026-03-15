package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.value.*;
import lombok.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 返信ドメインモデル
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class Reply {

    /**
     * 外部公開用の投稿 ID
     */
    ReplyId replyId;

    /**
     * 返信元の投稿
     */
    PostId postId;

    /**
     * 返信者
     */
    UserId userId;

    /**
     * 返信元の返信
     */
    @Nullable
    ReplyId parentReplyId;

    /**
     * 返信本文
     */
    Comment message;

    /**
     * 返信日時
     */
    LocalDateTime createdAt;

    /**
     * インスタンスを生成します。
     */
    public static Reply of(
            final ReplyId replyId,
            final PostId postId,
            final UserId userId,
            final ReplyId parentReplyId,
            final Comment message,
            final LocalDateTime createdAt
    ) {
        return new Reply(replyId, postId, userId, parentReplyId, message, createdAt);
    }

    /**
     * 返信が親返信を持つかどうかを判定します。
     *
     * @return 返信が親返信を持つ場合は true / それ以外は false
     */
    public boolean hasParentReply() {
        return Objects.nonNull(parentReplyId);
    }
}
