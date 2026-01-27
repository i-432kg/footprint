package jp.i432kg.footprint.domain.repository;

import jp.i432kg.footprint.domain.model.Replies;
import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.ReplyId;

public interface ReplyRepository {

    /**
     * 指定された投稿IDに関連付けられた返信を取得します。
     *
     * @param postId 取得対象の投稿ID
     * @return 指定された投稿IDに関連する返信のコレクション
     */
    Replies findRootsByPostId(final PostId postId);

    /**
     * 指定された返信IDに関連付けられた次の返信を取得します。
     *
     * @param parentReplyId 取得対象の返信ID
     * @return 指定された返信IDに関連する次の返信のコレクション
     */
    Replies findNextByParentReplyId(final ReplyId parentReplyId);

    /**
     * 新しい返信を保存する
     *
     * @param newReply 保存する新規返信情報
     */
    void saveReply(final Reply.NewReply newReply);

    /**
     * 返信の親にカウントを増やす
     *
     * @param replyId
     */
    void increaseReplyCount(final ReplyId replyId);
}
