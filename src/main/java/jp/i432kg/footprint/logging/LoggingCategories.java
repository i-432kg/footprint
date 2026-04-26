package jp.i432kg.footprint.logging;

/**
 * ログカテゴリ名を一元管理する定数クラスです。
 */
public final class LoggingCategories {

    /**
     * HTTP リクエスト/レスポンス単位のアクセスログカテゴリです。
     */
    public static final String ACCESS = "footprint.access";

    /**
     * 認証・認可イベントのログカテゴリです。
     */
    public static final String AUTH = "footprint.auth";

    /**
     * 業務処理・想定内異常・例外整形のログカテゴリです。
     */
    public static final String APP = "footprint.app";

    /**
     * 重要な状態変更操作の監査ログカテゴリです。
     */
    public static final String AUDIT = "footprint.audit";

    private LoggingCategories() {
    }
}
