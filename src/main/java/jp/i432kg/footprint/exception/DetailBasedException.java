package jp.i432kg.footprint.exception;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * `details` に正規項目を持つ独自例外の共通基底クラスです。
 * <p>
 * 本クラスは、`target` / `reason` / `rejectedValue` を中心とした
 * 構造化詳細情報を組み立てる helper を提供します。
 * domain / application 例外のうち、入力値や業務上の不整合を
 * 構造化して保持したい例外は本クラスを継承します。
 */
public abstract class DetailBasedException extends BaseException {

    /**
     * 何が問題になったかを示す正規項目キーです。
     */
    protected static final String KEY_TARGET = "target";

    /**
     * どういう理由で不正と判断したかを示す正規項目キーです。
     */
    protected static final String KEY_REASON = "reason";

    /**
     * 問題となった入力値や実値を示す正規項目キーです。
     */
    protected static final String KEY_REJECTED_VALUE = "rejectedValue";

    /**
     * 原因例外を持たない詳細付き例外を生成します。
     *
     * @param errorCode 機械判定用のエラーコード
     * @param message 人間向けの例外メッセージ
     * @param details 構造化詳細情報
     */
    protected DetailBasedException(
            final ErrorCode errorCode,
            final String message,
            final Map<String, Object> details
    ) {
        super(errorCode, message, details);
    }

    /**
     * 原因例外を保持する詳細付き例外を生成します。
     *
     * @param errorCode 機械判定用のエラーコード
     * @param message 人間向けの例外メッセージ
     * @param details 構造化詳細情報
     * @param cause 元になった例外
     */
    protected DetailBasedException(
            final ErrorCode errorCode,
            final String message,
            final Map<String, Object> details,
            final Throwable cause
    ) {
        super(errorCode, message, details, cause);
    }

    /**
     * `target` と `reason` から簡易的な例外メッセージを組み立てます。
     *
     * @param target 問題対象
     * @param reason 問題理由
     * @return 組み立てた例外メッセージ
     */
    protected static String message(final String target, final String reason) {
        return target + " is invalid. reason=" + reason;
    }

    /**
     * `target` と `reason` のみを持つ `details` を生成します。
     *
     * @param target 問題対象
     * @param reason 問題理由
     * @return 不変の詳細情報
     */
    protected static Map<String, Object> details(
            final String target,
            final String reason
    ) {
        final Map<String, Object> details = new LinkedHashMap<>();
        details.put(KEY_TARGET, target);
        details.put(KEY_REASON, reason);

        return Map.copyOf(details);
    }

    /**
     * 正規項目のみを持つ `details` を生成します。
     *
     * @param target 問題対象
     * @param reason 問題理由
     * @param rejectedValue 問題となった値
     * @return 不変の詳細情報
     */
    protected static Map<String, Object> details(
            final String target,
            final String reason,
            final Object rejectedValue
    ) {
        final Map<String, Object> details = new LinkedHashMap<>();
        details.put(KEY_TARGET, target);
        details.put(KEY_REASON, reason);
        details.put(KEY_REJECTED_VALUE, rejectedValue);

        return Map.copyOf(details);
    }

    /**
     * `target` と `reason` に加えて追加情報を持つ `details` を生成します。
     *
     * @param target 問題対象
     * @param reason 問題理由
     * @param extra 追加詳細情報
     * @return 不変の詳細情報
     */
    protected static Map<String, Object> details(
            final String target,
            final String reason,
            final Map<String, Object> extra
    ) {
        final Map<String, Object> details = new LinkedHashMap<>(details(target, reason));
        details.putAll(extra);

        return Map.copyOf(details);
    }

    /**
     * 正規項目に加えて追加情報を持つ `details` を生成します。
     *
     * @param target 問題対象
     * @param reason 問題理由
     * @param rejectedValue 問題となった値
     * @param extra 追加詳細情報
     * @return 不変の詳細情報
     */
    protected static Map<String, Object> details(
            final String target,
            final String reason,
            final Object rejectedValue,
            final Map<String, Object> extra
    ) {
        final Map<String, Object> details
                = new LinkedHashMap<>(details(target, reason, rejectedValue));
        details.putAll(extra);

        return Map.copyOf(details);
    }
}
