package jp.i432kg.footprint.domain.repository;

import jp.i432kg.footprint.domain.model.Post;

/**
 * 投稿に関するリポジトリインターフェース
 */
public interface PostRepository {

    /**
     * 新しい投稿を保存します。
     *
     * @param post 保存する新規投稿情報
     */
    void savePost(Post post);

}
