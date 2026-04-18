package jp.i432kg.footprint.presentation.api.response;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;

/**
 * 返信一覧で返却する返信レスポンス DTO です。
 */
@Value
@AllArgsConstructor(staticName = "of")
public class ReplyItemResponse {

    /**
     * 返信 ID です。
     */
    String id;

    /**
     * 紐づく投稿 ID です。
     */
    String postId;

    /**
     * 親返信 ID です。ルート返信では `null` になりえます。
     */
    @Nullable
    String parentReplyId;

    /**
     * 返信本文です。
     */
    String message;

    /**
     * 子返信数です。
     */
    Integer childCount;

    /**
     * 返信作成日時です。ISO 8601 形式で返却します。
     */
    OffsetDateTime createdAt;
}
