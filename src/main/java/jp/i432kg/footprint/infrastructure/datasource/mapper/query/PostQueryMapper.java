package jp.i432kg.footprint.infrastructure.datasource.mapper.query;

import jp.i432kg.footprint.application.query.model.PostSummary;
import jp.i432kg.footprint.domain.value.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jspecify.annotations.Nullable;

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
     * 最新の投稿一覧を検索する。
     *
     * @param lastId 最後に取得した投稿 ID
     * @param size 取得件数
     * @return 投稿の参照専用モデルのリスト
     */
    List<PostSummary> findRecentPosts(
            @Param("lastId") @Nullable PostId lastId,
            @Param("size") int size
    );

    /**
     * 自分の投稿一覧を検索する。
     *
     * @param userId ユーザー ID
     * @param lastId 最後に取得した投稿 ID
     * @param size 取得件数
     * @return 投稿の参照専用モデルのリスト
     */
    List<PostSummary> findMyPosts(
            @Param("userId") UserId userId,
            @Param("lastId") @Nullable PostId lastId,
            @Param("size") int size
    );

    /**
     * キーワードに基づいて投稿一覧を検索する。
     *
     * @param keyword 検索キーワード
     * @param lastId 最後に取得した投稿 ID
     * @param size 取得件数
     * @return 投稿の参照専用モデルのリスト
     */
    List<PostSummary> findPostsByKeyword(
            @Param("keyword") SearchKeyword keyword,
            @Param("lastId") @Nullable PostId lastId,
            @Param("size") int size
    );

    /**
     * 指定した境界ボックス内の投稿一覧を検索する。
     *
     * @param minLat 最小緯度
     * @param maxLat 最大緯度
     * @param minLng 最小経度
     * @param maxLng 最大経度
     * @return 投稿の参照専用モデルのリスト
     */
    List<PostSummary> findPostsByBBox(
            @Param("minLat") Latitude minLat,
            @Param("maxLat") Latitude maxLat,
            @Param("minLng") Longitude minLng,
            @Param("maxLng") Longitude maxLng
    );
}
