package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.value.ReplyId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.jspecify.annotations.Nullable;

/**
 * 親返信の有無を表す状態オブジェクトです。
 * <p>
 * ルート返信と子返信の違いを `null` ではなく明示的な状態として扱います。
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ParentReply {

    /**
     * 親返信が存在する場合の返信 ID
     */
    @Nullable
    ReplyId replyId;

    /**
     * ルート返信かどうか
     */
    boolean root;

    /**
     * 親返信を持つ状態オブジェクトを生成します。
     *
     * @param replyId 親返信 ID
     * @return 親返信付き状態オブジェクト
     */
    public static ParentReply of(final ReplyId replyId) {
        return new ParentReply(replyId, false);
    }

    /**
     * 親返信を持たないルート返信の状態オブジェクトを生成します。
     *
     * @return ルート返信状態オブジェクト
     */
    public static ParentReply root() {
        return new ParentReply(null, true);
    }

    /**
     * 親返信を持つ場合の返信 ID を返します。
     *
     * @return 親返信 ID。ルート返信の場合は {@code null}
     */
    public @Nullable ReplyId getReplyId() {
        return replyId;
    }

    /**
     * 親返信を持つかどうかを返します。
     *
     * @return 親返信を持つ場合は true
     */
    public boolean hasParent() {
        return !root;
    }
}
