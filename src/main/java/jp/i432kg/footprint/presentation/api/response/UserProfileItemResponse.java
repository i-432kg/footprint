package jp.i432kg.footprint.presentation.api.response;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * 現在ユーザーのプロフィール情報を表す API レスポンス DTO です。
 */
@Value
@AllArgsConstructor(staticName = "of")
public class UserProfileItemResponse {

    /**
     * ユーザー ID です。
     */
    String id;

    /**
     * ユーザー名です。
     */
    String username;

    /**
     * 登録メールアドレスです。
     */
    String email;

    /**
     * 投稿数です。
     */
    int postCount;

    /**
     * 返信数です。
     */
    int replyCount;
}
