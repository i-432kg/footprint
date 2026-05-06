package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.value.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 投稿ドメインモデル
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class Post {

    /**
     * 投稿の外部公開用 ID
     */
    PostId postId;

    /**
     * 投稿者
     */
    UserId userId;

    /**
     * 投稿画像
     */
    Image image;

    /**
     * 投稿コメント
     */
    PostComment caption;

    /**
     * 投稿日時
     */
    LocalDateTime createdAt;

    /**
     * 投稿ドメインモデルを生成します。
     *
     * @param postId    投稿の外部公開用 ID
     * @param userId    投稿者
     * @param image     投稿画像
     * @param caption   投稿コメント
     * @param createdAt 投稿日時
     * @return {@link Post} インスタンス
     */
    public static Post of(
            final PostId postId,
            final UserId userId,
            final Image image,
            final PostComment caption,
            final LocalDateTime createdAt
    ) {
        return new Post(postId, userId, image, caption, createdAt);
    }
}
