package jp.i432kg.footprint.application.exception;

import jp.i432kg.footprint.exception.DetailBasedException;
import jp.i432kg.footprint.exception.ErrorCode;

import java.util.Map;

/**
 * application 層で発生する独自例外の基底クラスです。
 * <p>
 * ユースケース実行失敗、リソース未検出、永続化や外部連携の失敗など、
 * application service の責務範囲で判定・変換される異常を表現します。
 * presentation 層では、本クラスの派生例外を
 * {@code ErrorCode} と構造化された {@code details} を持つ例外として扱います。
 */
public abstract class ApplicationException extends DetailBasedException {

    /**
     * 原因例外を持たない application 例外を生成します。
     *
     * @param errorCode 機械判定用のエラーコード
     * @param message 人間向けの例外メッセージ
     * @param details 構造化詳細情報
     */
    protected ApplicationException(
            final ErrorCode errorCode,
            final String message,
            final Map<String, Object> details
    ) {
        super(errorCode, message, details);
    }

    /**
     * 原因例外を保持する application 例外を生成します。
     *
     * @param errorCode 機械判定用のエラーコード
     * @param message 人間向けの例外メッセージ
     * @param details 構造化詳細情報
     * @param cause 元になった例外
     */
    protected ApplicationException(
            final ErrorCode errorCode,
            final String message,
            final Map<String, Object> details,
            final Throwable cause
    ) {
        super(errorCode, message, details, cause);
    }
}
