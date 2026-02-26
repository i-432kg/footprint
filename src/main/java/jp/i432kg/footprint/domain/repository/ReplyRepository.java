package jp.i432kg.footprint.domain.repository;

import jp.i432kg.footprint.domain.model.Replies;
import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.domain.value.UserId;

public interface ReplyRepository {

    /**
     * 新しい返信を保存する
     *
     * @param newReply 保存する新規返信情報
     */
    void saveReply(Reply.NewReply newReply);

    /**
     * 返信の親にカウントを増やす
     *
     * @param replyId
     */
    void increaseReplyCount(ReplyId replyId);

    Replies findMyReplies(UserId userId);
}
