package jp.i432kg.footprint.presentation.api.response;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.OffsetDateTime;

/**
 * 返信アイテムのレスポンス
 */
@Value
@AllArgsConstructor(staticName = "of")
public class ReplyItemResponse {

    /**
     * 返信 ID
     */
    String id;

    /**
     * 投稿 ID
     */
    String postId;

    /**
     * この返信の親返信 ID
     */
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
     * 投稿作成日時
     * ISO 8601 形式の日時フォーマット
     */
    OffsetDateTime createdAt;
}
