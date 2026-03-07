package jp.i432kg.footprint.domain.repository;

import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.value.PostId;

/**
 * 投稿に関するリポジトリインターフェース
 */
public interface PostRepository {

    /**
     * 指定の投稿 IDを持つ有効な投稿が存在するかを確認します。
     *
     * @param postId 投稿 ID
     * @return 存在する場合は true / 存在しない場合は false
     */
    boolean existsById(PostId postId);

    /**
     * 新しい投稿を保存します。
     *
     * @param post 保存する新規投稿情報
     */
    void savePost(Post post);

}
