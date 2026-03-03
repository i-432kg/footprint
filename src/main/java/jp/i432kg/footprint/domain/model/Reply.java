package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.value.*;
import lombok.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * 返信ドメインモデル
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class Reply {

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
            final PostId postId,
            final UserId userId,
            final ReplyId parentReplyId,
            final Comment message,
            final LocalDateTime createdAt
    ) {
        return new Reply(postId, userId, parentReplyId, message, createdAt);
    }
}
