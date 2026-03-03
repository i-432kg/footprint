package jp.i432kg.footprint.application.command.model;

import jp.i432kg.footprint.domain.value.Comment;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.domain.value.UserId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * 返信作成を指示するコマンドオブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateReplyCommand {

    /**
     * 返信元の投稿
     */
    @NonNull
    PostId postId;

    /**
     * 返信者
     */
    @NonNull
    UserId userId;

    /**
     * 返信元の返信
     */
    @Nullable
    ReplyId parentReplyId;

    /**
     * 返信本文
     */
    @NonNull
    Comment message;

    /**
     * {@link CreateReplyCommand} インスタンスを生成します。
     *
     * @param postId        返信元の投稿
     * @param userId        返信者
     * @param parentReplyId 返信元の返信
     * @param message       返信本文
     * @return {@link CreateReplyCommand} インスタンス
     */
    public static CreateReplyCommand of(
            final @NonNull PostId postId,
            final @NonNull UserId userId,
            final @Nullable ReplyId parentReplyId,
            final @NonNull Comment message
    ) {
        return new CreateReplyCommand(postId, userId, parentReplyId, message);
    }

    public boolean hasParentReply() {
        return Objects.nonNull(parentReplyId);
    }
}
