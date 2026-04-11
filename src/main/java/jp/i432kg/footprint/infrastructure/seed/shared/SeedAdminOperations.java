package jp.i432kg.footprint.infrastructure.seed.shared;

import java.util.Optional;

/**
 * fixed seed scenario の存在確認と再取得に必要な永続化操作を表す契約です。
 */
public interface SeedAdminOperations {

    /**
     * 指定メールアドレスに対応する seed ユーザー ID を返します。
     *
     * @param email 検索対象メールアドレス
     * @return 該当ユーザー ID
     */
    Optional<String> findUserIdByEmail(String email);

    /**
     * 指定 caption に対応する seed 投稿 ID を返します。
     *
     * @param caption 検索対象 caption
     * @return 該当投稿 ID
     */
    Optional<String> findPostIdByCaption(String caption);

    /**
     * 指定投稿 ID と返信メッセージに対応する seed 返信 ID を返します。
     *
     * @param postId 検索対象投稿 ID
     * @param message 検索対象返信メッセージ
     * @return 該当返信 ID
     */
    Optional<String> findReplyIdByPostIdAndMessage(String postId, String message);

    /**
     * 指定投稿 ID と返信メッセージの組み合わせを持つ seed 返信が存在するかを返します。
     *
     * @param postId 検索対象投稿 ID
     * @param message 検索対象返信メッセージ
     * @return 存在する場合は true
     */
    boolean existsReplyByPostIdAndMessage(String postId, String message);
}
