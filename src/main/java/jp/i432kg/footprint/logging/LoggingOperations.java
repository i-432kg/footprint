package jp.i432kg.footprint.logging;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * ログ event 解決に利用する operation 名を一元管理する定数クラスです。
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LoggingOperations {

    /**
     * 投稿作成操作です。
     */
    public static final String POST_CREATE = "POST_CREATE";

    /**
     * 返信作成操作です。
     */
    public static final String REPLY_CREATE = "REPLY_CREATE";

    /**
     * ユーザー登録操作です。
     */
    public static final String USER_CREATE = "USER_CREATE";

    /**
     * 最新投稿一覧取得操作です。
     */
    public static final String POST_TIMELINE_FETCH = LoggingEvents.POST_TIMELINE_FETCH;

    /**
     * キーワード投稿検索操作です。
     */
    public static final String POST_SEARCH_FETCH = LoggingEvents.POST_SEARCH_FETCH;

    /**
     * 地図範囲投稿検索操作です。
     */
    public static final String POST_MAP_BBOX_FETCH = LoggingEvents.POST_MAP_BBOX_FETCH;

    /**
     * 投稿詳細取得操作です。
     */
    public static final String POST_DETAIL_FETCH = LoggingEvents.POST_DETAIL_FETCH;

    /**
     * 返信一覧取得操作です。
     */
    public static final String REPLY_LIST_FETCH = LoggingEvents.REPLY_LIST_FETCH;

    /**
     * 現在ユーザープロフィール取得操作です。
     */
    public static final String ME_FETCH = LoggingEvents.ME_FETCH;

    /**
     * 現在ユーザー自身の投稿一覧取得操作です。
     */
    public static final String ME_POSTS_FETCH = LoggingEvents.ME_POSTS_FETCH;

    /**
     * 現在ユーザー自身の返信一覧取得操作です。
     */
    public static final String ME_REPLIES_FETCH = LoggingEvents.ME_REPLIES_FETCH;
}
