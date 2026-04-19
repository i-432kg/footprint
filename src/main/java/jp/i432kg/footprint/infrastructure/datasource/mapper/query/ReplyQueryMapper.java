package jp.i432kg.footprint.infrastructure.datasource.mapper.query;

import jp.i432kg.footprint.application.query.model.ReplySummary;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.domain.value.UserId;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * 返信参照用の MyBatis マッパーインターフェース。
 */
@Mapper
public interface ReplyQueryMapper {

    /**
     * 投稿に紐付くトップレベル返信一覧を検索する。
     *
     * @param postId 投稿 ID
     * @return 返信の参照専用モデルのリスト
     */
    List<ReplySummary> findTopLevelRepliesByPostId(@Param("postId") PostId postId);

    /**
     * 指定した親返信に紐付く子返信一覧を検索する。
     *
     * @param parentReplyId 親返信 ID
     * @return 返信の参照専用モデルのリスト
     */
    List<ReplySummary> findNestedRepliesByParentId(@Param("parentReplyId") ReplyId parentReplyId);

    /**
     * ユーザーが投稿した返信一覧を検索する。
     *
     * @param userId ユーザー ID
     * @param lastId 最後に取得した返信 ID
     * @param size 取得件数
     * @return 返信の参照専用モデルのリスト
     */
    List<ReplySummary> findMyReplies(
            @Param("userId") UserId userId,
            @Param("lastId") @Nullable ReplyId lastId,
            @Param("size") int size
    );
}
