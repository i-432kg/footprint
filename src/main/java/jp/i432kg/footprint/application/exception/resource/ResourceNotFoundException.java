package jp.i432kg.footprint.application.exception.resource;

import jp.i432kg.footprint.application.exception.ApplicationException;
import jp.i432kg.footprint.exception.ErrorCode;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * application 層で扱う「リソース未検出」例外の基底クラスです。
 * <p>
 * 投稿、返信、ユーザーなどの参照対象が存在しない場合に利用します。
 * `details` には `target`, `reason=not_found`, `resourceId` を持つ正規形を採用します。
 */
public abstract class ResourceNotFoundException extends ApplicationException {

    /**
     * リソース未検出例外を生成します。
     *
     * @param errorCode 機械判定用のエラーコード
     * @param message 人間向けの例外メッセージ
     * @param details 構造化詳細情報
     */
    protected ResourceNotFoundException(
            final ErrorCode errorCode,
            final String message,
            final Map<String, Object> details
    ) {
        super(errorCode, message, details);
    }

    /**
     * 未検出リソース向けの `details` を生成します。
     *
     * @param target 未検出となったリソース種別
     * @param resourceId 未検出リソースの公開識別子
     * @return 不変の詳細情報
     */
    protected static Map<String, Object> details(final String target, final String resourceId) {
        final Map<String, Object> details = new LinkedHashMap<>();
        details.put("target", Objects.requireNonNull(target));
        details.put("reason", "not_found");
        details.put("resourceId", Objects.requireNonNull(resourceId));
        return Map.copyOf(details);
    }
}
