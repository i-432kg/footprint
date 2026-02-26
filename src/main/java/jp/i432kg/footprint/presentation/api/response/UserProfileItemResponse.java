package jp.i432kg.footprint.presentation.api.response;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * ユーザーのプロフィールアイテムのレスポンス
 */
@Value
@AllArgsConstructor(staticName = "of")
public class UserProfileItemResponse {

    /**
     * ユーザー ID
     */
    Integer id;

    /**
     * ユーザー名
     */
    String username;

    /**
     * 登録メールアドレス
     */
    String email;

    /**
     * 投稿数
     */
    int postCount;

    /**
     * 返信数
     */
    int replyCount;
}
