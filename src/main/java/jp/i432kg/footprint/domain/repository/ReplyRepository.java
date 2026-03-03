package jp.i432kg.footprint.domain.repository;

import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.value.ReplyId;

/**
 * 返信に関するリポジトリインターフェース
 */
public interface ReplyRepository {

    /**
     * 新しい返信を保存します。
     *
     * @param reply 保存する新規返信情報
     */
    void saveReply(Reply reply);

    /**
     * 返信先が持つ返信数を1つ増やします。
     *
     * @param replyId 返信先の返信 ID
     */
    void increaseReplyCount(ReplyId replyId);
}
