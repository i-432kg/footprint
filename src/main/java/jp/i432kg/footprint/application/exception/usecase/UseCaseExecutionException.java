package jp.i432kg.footprint.application.exception.usecase;

import jp.i432kg.footprint.application.exception.ApplicationException;
import jp.i432kg.footprint.exception.ErrorCode;

import java.util.Map;

/**
 * application 層のユースケース実行失敗を表す基底例外です。
 * <p>
 * 投稿作成、返信作成、ユーザー作成などの application service が、
 * 永続化、外部 I/O、後処理などの過程で失敗した場合に利用します。
 */
public abstract class UseCaseExecutionException extends ApplicationException {

    /**
     * 原因例外を持たないユースケース失敗例外を生成します。
     *
     * @param errorCode 機械判定用のエラーコード
     * @param message 人間向けの例外メッセージ
     * @param details 構造化詳細情報
     */
    protected UseCaseExecutionException(
            final ErrorCode errorCode,
            final String message,
            final Map<String, Object> details
    ) {
        super(errorCode, message, details);
    }

    /**
     * 原因例外を保持するユースケース失敗例外を生成します。
     *
     * @param errorCode 機械判定用のエラーコード
     * @param message 人間向けの例外メッセージ
     * @param details 構造化詳細情報
     * @param cause 元になった例外
     */
    protected UseCaseExecutionException(
            final ErrorCode errorCode,
            final String message,
            final Map<String, Object> details,
            final Throwable cause
    ) {
        super(errorCode, message, details, cause);
    }
}
