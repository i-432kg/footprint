package jp.i432kg.footprint.logging;

import lombok.NoArgsConstructor;

/**
 * ログイベント名を一元管理する定数クラスです。
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class LoggingEvents {

    /**
     * HTTP リクエスト共通の access ログイベントです。
     */
    public static final String HTTP_ACCESS = "HTTP_ACCESS";

    /**
     * 認証成功イベントです。
     */
    public static final String AUTH_LOGIN_SUCCESS = "AUTH_LOGIN_SUCCESS";

    /**
     * 認証失敗イベントです。
     */
    public static final String AUTH_LOGIN_FAILURE = "AUTH_LOGIN_FAILURE";

    /**
     * 未認証アクセスイベントです。
     */
    public static final String AUTH_UNAUTHORIZED = "AUTH_UNAUTHORIZED";

    /**
     * 認可拒否イベントです。
     */
    public static final String AUTH_FORBIDDEN = "AUTH_FORBIDDEN";

    /**
     * CSRF 拒否イベントです。
     */
    public static final String AUTH_CSRF_REJECTED = "AUTH_CSRF_REJECTED";

    /**
     * 投稿作成成功イベントです。
     */
    public static final String POST_CREATE_SUCCESS = "POST_CREATE_SUCCESS";

    /**
     * 最新投稿一覧取得イベントです。
     */
    public static final String POST_TIMELINE_FETCH = "POST_TIMELINE_FETCH";

    /**
     * キーワード投稿検索イベントです。
     */
    public static final String POST_SEARCH_FETCH = "POST_SEARCH_FETCH";

    /**
     * 地図範囲投稿検索イベントです。
     */
    public static final String POST_MAP_BBOX_FETCH = "POST_MAP_BBOX_FETCH";

    /**
     * 投稿詳細取得イベントです。
     */
    public static final String POST_DETAIL_FETCH = "POST_DETAIL_FETCH";

    /**
     * 返信作成成功イベントです。
     */
    public static final String REPLY_CREATE_SUCCESS = "REPLY_CREATE_SUCCESS";

    /**
     * 返信一覧取得イベントです。
     */
    public static final String REPLY_LIST_FETCH = "REPLY_LIST_FETCH";

    /**
     * ユーザー登録成功イベントです。
     */
    public static final String USER_CREATE_SUCCESS = "USER_CREATE_SUCCESS";

    /**
     * 現在ユーザープロフィール取得イベントです。
     */
    public static final String ME_FETCH = "ME_FETCH";

    /**
     * 現在ユーザー自身の投稿一覧取得イベントです。
     */
    public static final String ME_POSTS_FETCH = "ME_POSTS_FETCH";

    /**
     * 現在ユーザー自身の返信一覧取得イベントです。
     */
    public static final String ME_REPLIES_FETCH = "ME_REPLIES_FETCH";
}
