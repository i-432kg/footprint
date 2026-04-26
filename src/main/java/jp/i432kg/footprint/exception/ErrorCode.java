package jp.i432kg.footprint.exception;

/**
 * 独自例外を機械判定用に分類するエラーコードです。
 * <p>
 * 本 enum は、独自例外の種類を安定した識別子として表現するために利用します。
 * API エラー応答では `ProblemDetail` の拡張プロパティ `errorCode` として返却され、
 * ログやテストでも分類キーとして参照します。
 */
public enum ErrorCode {

    /**
     * 投稿作成ユースケースの実行失敗です。
     */
    POST_COMMAND_FAILED,

    /**
     * 返信作成ユースケースの実行失敗です。
     */
    REPLY_COMMAND_FAILED,

    /**
     * ユーザー作成ユースケースの実行失敗です。
     */
    USER_COMMAND_FAILED,

    /**
     * 値オブジェクト単位の入力値不正です。
     */
    DOMAIN_INVALID_VALUE,

    /**
     * ドメインモデル全体の不変条件違反です。
     */
    DOMAIN_INVALID_MODEL,

    /**
     * 指定された投稿が存在しないことを表します。
     */
    POST_NOT_FOUND,

    /**
     * 指定された返信が存在しないことを表します。
     */
    REPLY_NOT_FOUND,

    /**
     * 指定されたユーザーが存在しないことを表します。
     */
    USER_NOT_FOUND,

    /**
     * 親返信が期待した投稿に属していない不整合です。
     */
    REPLY_POST_MISMATCH,

    /**
     * 既に使用済みのメールアドレスであることを表します。
     */
    EMAIL_ALREADY_USED,

    /**
     * 想定外の失敗や内部エラーを表す汎用コードです。
     */
    UNEXPECTED_ERROR
}
