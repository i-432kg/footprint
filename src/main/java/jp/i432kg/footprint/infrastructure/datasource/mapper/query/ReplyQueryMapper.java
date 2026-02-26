package jp.i432kg.footprint.infrastructure.datasource.mapper.query;

import jp.i432kg.footprint.application.query.model.ReplySummary;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.ReplyId;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 返信情報の参照専用クエリを行う Mapper インターフェース
 */
@Mapper
public interface ReplyQueryMapper {

    /**
     * 投稿に紐付くトップレベル（親なし）の返信一覧を検索します。
     *
     * @param postId 投稿 ID
     * @return 返信の参照専用モデルのリスト
     */
    List<ReplySummary> findTopLevelRepliesByPostId(@Param("postId") PostId postId);

    /**
     * 特定の返信に紐付く子返信一覧を検索します。
     *
     * @param parentReplyId 親返信 ID
     * @return 返信の参照専用モデルのリスト
     */
    List<ReplySummary> findNestedRepliesByParentId(@Param("parentReplyId") ReplyId parentReplyId);
}
