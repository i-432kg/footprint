package jp.i432kg.footprint.infrastructure.seed;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * STG seed の実行・削除で利用する管理用 MyBatis Mapper。
 * <p>
 * 既存データの重複判定や、seed データ削除対象の取得・削除を担う。
 * </p>
 */
@Mapper
public interface SeedAdminMapper {

    /**
     * メールアドレスから seed ユーザーのユーザー ID を取得する。
     *
     * @param email 検索対象のメールアドレス
     * @return ユーザー ID。存在しない場合は空
     */
    Optional<String> findUserIdByEmail(@Param("email") String email);

    /**
     * 投稿本文から seed 投稿の投稿 ID を取得する。
     *
     * @param caption 検索対象の投稿本文
     * @return 投稿 ID。存在しない場合は空
     */
    Optional<String> findPostIdByCaption(@Param("caption") String caption);

    /**
     * 指定した投稿 ID とメッセージを持つ返信が既に存在するかを判定する。
     *
     * @param postId 対象投稿 ID
     * @param message 返信メッセージ
     * @return 既に存在する場合は true
     */
    boolean existsReplyByPostIdAndMessage(@Param("postId") String postId, @Param("message") String message);

    /**
     * seed 画像として登録されたオブジェクトキー一覧を取得する。
     *
     * @return seed 画像のオブジェクトキー一覧
     */
    List<String> findSeedImageObjectKeys();

    /**
     * seed 返信を削除する。
     */
    void deleteSeedReplies();

    /**
     * seed 投稿画像を削除する。
     */
    void deleteSeedPostImages();

    /**
     * seed 投稿を削除する。
     */
    void deleteSeedPosts();

    /**
     * seed ユーザーを削除する。
     */
    void deleteSeedUsers();
}