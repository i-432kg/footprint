package jp.i432kg.footprint.infrastructure.seed.local;

import jp.i432kg.footprint.infrastructure.seed.shared.SeedAdminOperations;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * local seed データの存在確認とクリーンアップに使用する MyBatis mapper です。
 */
@Mapper
public interface LocalSeedAdminMapper extends SeedAdminOperations {

    /**
     * 指定メールアドレスに対応する seed ユーザー ID を取得します。
     *
     * @param email 検索対象メールアドレス
     * @return 該当ユーザー ID
     */
    Optional<String> findUserIdByEmail(@Param("email") String email);

    /**
     * 指定 caption に対応する seed 投稿 ID を取得します。
     *
     * @param caption 検索対象 caption
     * @return 該当投稿 ID
     */
    Optional<String> findPostIdByCaption(@Param("caption") String caption);

    /**
     * 指定投稿とメッセージに対応する seed 返信 ID を取得します。
     *
     * @param postId 検索対象投稿 ID
     * @param message 検索対象返信メッセージ
     * @return 該当返信 ID
     */
    Optional<String> findReplyIdByPostIdAndMessage(@Param("postId") String postId, @Param("message") String message);

    /**
     * 指定投稿とメッセージの組み合わせを持つ seed 返信が存在するかを返します。
     *
     * @param postId 検索対象投稿 ID
     * @param message 検索対象返信メッセージ
     * @return 存在する場合は true
     */
    boolean existsReplyByPostIdAndMessage(@Param("postId") String postId, @Param("message") String message);

    /**
     * local seed が作成した投稿画像の object key 一覧を返します。
     *
     * @return 投稿画像 object key 一覧
     */
    List<String> findSeedImageObjectKeys();

    /**
     * local seed が作成した返信を削除します。
     */
    void deleteSeedReplies();

    /**
     * local seed が作成した投稿画像レコードを削除します。
     */
    void deleteSeedPostImages();

    /**
     * local seed が作成した投稿を削除します。
     */
    void deleteSeedPosts();

    /**
     * local seed が作成したユーザーを削除します。
     */
    void deleteSeedUsers();
}
