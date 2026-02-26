package jp.i432kg.footprint.application.query;

import jp.i432kg.footprint.application.query.model.ReplySummary;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.ReplyId;

import java.util.List;

/**
 * 返信の参照サービス
 */
public interface ReplyQueryService {

    /**
     * 指定された投稿IDに関連付けられたトップレベルの返信一覧を取得する。
     *
     * @param postId 取得対象の投稿 ID
     * @return 返信の一覧（1階層のみ。存在しない場合は空リスト）
     */
    List<ReplySummary> listTopLevelReplies(PostId postId);

    /**
     * 指定された返信IDに関連付けられた次の返信一覧（1階層のみ）を取得する。
     *
     * @param parentReplyId 取得対象の返信 ID
     * @return 返信の一覧（1階層のみ。存在しない場合は空リスト）
     */
    List<ReplySummary> listNestedReplies(ReplyId parentReplyId);

}
