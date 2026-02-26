package jp.i432kg.footprint.infrastructure.datasource.mapper.query;

import jp.i432kg.footprint.application.query.model.PostSummary;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.SearchKeyword;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * 投稿情報の参照専用クエリを行う Mapper インターフェース
 */
@Mapper
public interface PostQueryMapper {

    /**
     * 投稿IDで投稿を検索します。
     *
     * @param postId 投稿 ID
     * @return 投稿の参照専用モデル
     */
    Optional<PostSummary> findPostById(@Param("postId") PostId postId);

    /**
     * 最新の投稿一覧を検索します。
     *
     * @param lastId 最後に取得した投稿のID（シーク用）
     * @param size   取得件数
     * @return 投稿の参照専用モデルのリスト
     */
    List<PostSummary> findRecentPosts(
            @Param("lastId") @Nullable PostId lastId,
            @Param("size") int size
    );

    /**
     * キーワードに基づいて投稿一覧を検索します。
     *
     * @param keyword 検索キーワード
     * @param lastId  最後に取得した投稿のID（シーク用）
     * @param size    取得件数
     * @return 投稿の参照専用モデルのリスト
     */
    List<PostSummary> findPostsByKeyword(
            @Param("keyword") SearchKeyword keyword,
            @Param("lastId") @Nullable PostId lastId,
            @Param("size") int size
    );
}
