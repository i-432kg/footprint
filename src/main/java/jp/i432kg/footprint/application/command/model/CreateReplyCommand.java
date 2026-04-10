package jp.i432kg.footprint.application.command.model;

import jp.i432kg.footprint.domain.value.Comment;
import jp.i432kg.footprint.domain.model.ParentReply;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.UserId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * 返信作成を指示するコマンドオブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateReplyCommand {

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
    ParentReply parentReply;

    /**
     * 返信本文
     */
    Comment message;

    /**
     * {@link CreateReplyCommand} インスタンスを生成します。
     *
     * @param postId        返信元の投稿
     * @param userId        返信者
     * @param parentReply 返信元の返信状態
     * @param message       返信本文
     * @return {@link CreateReplyCommand} インスタンス
     */
    public static CreateReplyCommand of(
            final PostId postId,
            final UserId userId,
            final ParentReply parentReply,
            final Comment message
    ) {
        return new CreateReplyCommand(postId, userId, parentReply, message);
    }
}
