package jp.i432kg.footprint.application.query.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * 返信の参照専用モデル
 */
@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ReplySummary {

    /**
     * 返信 ID
     */
    String id;

    /**
     * 投稿 ID
     */
    String postId;

    /**
     * この返信の作成者
     */
    String userId;

    /**
     * この返信の親返信 ID
     */
    @Nullable
    String parentReplyId;

    /**
     * 返信本文
     */
    String message;

    /**
     * この返信の子返信の数
     */
    Integer childCount;

    /**
     * 返信作成日時
     */
    LocalDateTime createdAt;
}
