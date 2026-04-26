package jp.i432kg.footprint.domain.exception;

import jp.i432kg.footprint.exception.DetailBasedException;
import jp.i432kg.footprint.exception.ErrorCode;

import java.util.Map;

/**
 * ドメイン層で発生する独自例外の基底クラスです。
 * <p>
 * 値オブジェクトの検証失敗、ドメインモデルの不変条件違反、業務ルール上の不整合など、
 * ドメイン知識に基づいて判定される異常を表現します。
 * application 層や presentation 層では、本クラスの派生例外を
 * {@code ErrorCode} と構造化された {@code details} を持つ例外として扱います。
 */
public abstract class DomainException extends DetailBasedException {

    /**
     * 原因例外を持たないドメイン例外を生成します。
     *
     * @param errorCode 機械判定用のエラーコード
     * @param message 人間向けの例外メッセージ
     * @param details 構造化詳細情報
     */
    protected DomainException(
            final ErrorCode errorCode,
            final String message,
            final Map<String, Object> details
    ) {
        super(errorCode, message, details);
    }

    /**
     * 原因例外を保持するドメイン例外を生成します。
     *
     * @param errorCode 機械判定用のエラーコード
     * @param message 人間向けの例外メッセージ
     * @param details 構造化詳細情報
     * @param cause 元になった例外
     */
    protected DomainException(
            final ErrorCode errorCode,
            final String message,
            final Map<String, Object> details,
            final Throwable cause
    ) {
        super(errorCode, message, details, cause);
    }
}
