package jp.i432kg.footprint.exception;

import lombok.Getter;

import java.util.Map;
import java.util.Objects;

/**
 * プロジェクト内の独自例外が共通して持つ情報を表す基底クラスです。
 * <p>
 * すべての独自例外は、機械判定用の {@link ErrorCode} と、
 * API エラー応答やログへ引き渡すための構造化情報 {@code details} を保持します。
 * {@code details} は {@link Map#copyOf(Map)} で不変化されるため、
 * 呼び出し側は null ではなく明示的な Map を渡す必要があります。
 */
@Getter
public abstract class BaseException extends RuntimeException {

    /**
     * 機械判定用の安定したエラーコードです。
     */
    private final ErrorCode errorCode;

    /**
     * API エラー応答やログ出力で利用する構造化詳細情報です。
     */
    private final Map<String, Object> details;

    /**
     * 原因例外を持たない独自例外を生成します。
     *
     * @param errorCode 機械判定用のエラーコード
     * @param message 人間向けの例外メッセージ
     * @param details 構造化詳細情報
     */
    protected BaseException(
            final ErrorCode errorCode,
            final String message,
            final Map<String, Object> details
    ) {
        super(message);
        this.errorCode = Objects.requireNonNull(errorCode);
        this.details = Map.copyOf(details);
    }

    /**
     * 原因例外を保持する独自例外を生成します。
     *
     * @param errorCode 機械判定用のエラーコード
     * @param message 人間向けの例外メッセージ
     * @param details 構造化詳細情報
     * @param cause 元になった例外
     */
    protected BaseException(
            final ErrorCode errorCode,
            final String message,
            final Map<String, Object> details,
            final Throwable cause
    ) {
        super(message, cause);
        this.errorCode = Objects.requireNonNull(errorCode);
        this.details = Map.copyOf(details);
    }

}
