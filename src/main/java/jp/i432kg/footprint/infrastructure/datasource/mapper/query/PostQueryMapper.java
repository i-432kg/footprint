package jp.i432kg.footprint.infrastructure.datasource.mapper.query;

import jp.i432kg.footprint.application.query.model.PostSummary;
import jp.i432kg.footprint.domain.model.BoundingBox;
import jp.i432kg.footprint.domain.value.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 投稿参照用の MyBatis マッパーインターフェース。
 */
@Mapper
public interface PostQueryMapper {

    /**
     * 投稿 ID で投稿を検索する。
     *
     * @param postId 投稿 ID
     * @return 投稿の参照専用モデル
     */
    Optional<PostSummary> findPostById(@Param("postId") PostId postId);

    /**
     * 最新の投稿一覧の先頭ページを検索する。
     *
     * @param size 取得件数
     * @return 投稿の参照専用モデルのリスト
     */
    List<PostSummary> findRecentPostsFirstPage(@Param("size") int size);

    /**
     * 最新の投稿一覧をシーク継続取得する。
     *
     * @param lastId 最後に取得した投稿 ID
     * @param size 取得件数
     * @return 投稿の参照専用モデルのリスト
     */
    List<PostSummary> findRecentPostsAfterCursor(
            @Param("lastId") PostId lastId,
            @Param("size") int size
    );

    /**
     * 自分の投稿一覧の先頭ページを検索する。
     *
     * @param userId ユーザー ID
     * @param size 取得件数
     * @return 投稿の参照専用モデルのリスト
     */
    List<PostSummary> findMyPostsFirstPage(
            @Param("userId") UserId userId,
            @Param("size") int size
    );

    /**
     * 自分の投稿一覧をシーク継続取得する。
     *
     * @param userId ユーザー ID
     * @param lastId 最後に取得した投稿 ID
     * @param size 取得件数
     * @return 投稿の参照専用モデルのリスト
     */
    List<PostSummary> findMyPostsAfterCursor(
            @Param("userId") UserId userId,
            @Param("lastId") PostId lastId,
            @Param("size") int size
    );

    /**
     * キーワードに基づく投稿一覧の先頭ページを検索する。
     *
     * @param keyword 検索キーワード
     * @param size 取得件数
     * @return 投稿の参照専用モデルのリスト
     */
    List<PostSummary> findPostsByKeywordFirstPage(
            @Param("keyword") SearchKeyword keyword,
            @Param("size") int size
    );

    /**
     * キーワードに基づく投稿一覧をシーク継続取得する。
     *
     * @param keyword 検索キーワード
     * @param lastId 最後に取得した投稿 ID
     * @param size 取得件数
     * @return 投稿の参照専用モデルのリスト
     */
    List<PostSummary> findPostsByKeywordAfterCursor(
            @Param("keyword") SearchKeyword keyword,
            @Param("lastId") PostId lastId,
            @Param("size") int size
    );

    /**
     * 指定した境界ボックス内の投稿一覧を検索する。
     *
     * @param boundingBox 検索対象の境界ボックス
     * @return 投稿の参照専用モデルのリスト
     */
    List<PostSummary> findPostsByBBox(@Param("boundingBox") BoundingBox boundingBox);
}
