package jp.i432kg.footprint.logging;

/**
 * ログイベント名を一元管理する定数クラスです。
 */
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
     * 返信作成成功イベントです。
     */
    public static final String REPLY_CREATE_SUCCESS = "REPLY_CREATE_SUCCESS";

    /**
     * ユーザー登録成功イベントです。
     */
    public static final String USER_CREATE_SUCCESS = "USER_CREATE_SUCCESS";

    private LoggingEvents() {
    }
}
