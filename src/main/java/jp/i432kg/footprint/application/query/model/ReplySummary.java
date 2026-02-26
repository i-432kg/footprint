package jp.i432kg.footprint.application.query.model;

import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * 返信の参照専用モデル
 */
@Value
@NoArgsConstructor(force = true)
public class ReplySummary {


    /**
     * 返信 ID
     */
    Integer id;

    /**
     * 投稿 ID
     */
    Integer postId;

    /**
     * この返信の親返信 ID
     */
    Integer parentReplyId;

    /**
     * 返信本文
     */
    String body;

    /**
     * この返信の子返信の数
     */
    Integer childCount;

    /**
     * 返信作成日時
     */
    LocalDateTime createdAt;
}
