package jp.i432kg.footprint.application.query;

import jp.i432kg.footprint.application.exception.resource.PostNotFoundException;
import jp.i432kg.footprint.application.query.model.PostSummary;
import jp.i432kg.footprint.domain.value.*;

import java.util.List;
import java.util.Optional;

/**
 * 投稿の参照サービス
 */
public interface PostQueryService {

    /**
     * 最新の投稿一覧を取得する。
     *
     * @param lastId 最後の投稿 ID
     * @param size   取得する投稿数
     * @return 最新の投稿一覧（存在しない場合は空リスト）
     */
    List<PostSummary> listRecentPosts(PostId lastId, int size);

    /**
     * 自分の投稿一覧を取得する。
     *
     * @param userId 自分のユーザー ID
     * @param lastId 最後の投稿 ID
     * @param size   取得する投稿数
     * @return 最新の投稿一覧（存在しない場合は空リスト）
     */
    List<PostSummary> listMyPosts(UserId userId, PostId lastId, int size);

    /**
     * 検索キーワードで投稿を検索する。
     *
     * @param keyword 検索キーワード
     * @param lastId  最後の投稿 ID
     * @param size    取得する投稿数
     * @return 投稿の検索結果一覧（該当なしの場合は空リスト）
     */
    List<PostSummary> searchPosts(SearchKeyword keyword, PostId lastId, int size);

    /**
     * 指定された座標範囲（緯度経度の境界ボックス）に含まれる位置情報を持つ投稿一覧を取得します。
     *
     * @param minLat 最小緯度
     * @param maxLat 最大緯度
     * @param minLng 最小経度
     * @param maxLng 最大経度
     * @return 範囲内の投稿リスト
     */
    List<PostSummary> searchPostsByBBox(
            Latitude minLat,
            Latitude maxLat,
            Longitude minLng,
            Longitude maxLng
    );

    /**
     * 投稿を取得する。
     * 投稿が存在しない場合は例外をスローします。
     *
     * @param postId 投稿 ID
     * @return 投稿
     * @throws PostNotFoundException 投稿が存在しない場合
     */
    PostSummary getPost(PostId postId) throws PostNotFoundException;

    /**
     * 投稿を検索する。
     *
     * @param postId 投稿 ID
     * @return 投稿（存在しない場合は empty）
     */
    Optional<PostSummary> findPost(PostId postId);

}
