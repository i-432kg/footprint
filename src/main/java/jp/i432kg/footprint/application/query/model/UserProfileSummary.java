package jp.i432kg.footprint.application.query.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

/**
 * ユーザープロフィールの参照専用モデル
 */
@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserProfileSummary {

    /**
     * ユーザー ID
     */
    String id;

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
