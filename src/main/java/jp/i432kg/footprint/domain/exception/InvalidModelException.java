package jp.i432kg.footprint.domain.exception;

import jp.i432kg.footprint.exception.ErrorCode;

import java.util.Map;

/**
 * ドメインモデル全体の不変条件違反を表す例外です。
 * <p>
 * 単一の値オブジェクト検証ではなく、複数の値の組み合わせやモデル全体の整合性が
 * ドメインルールを満たさない場合に利用します。
 */
public class InvalidModelException extends DomainException {

    private InvalidModelException(final String message, final Map<String, Object> details) {
        super(ErrorCode.DOMAIN_INVALID_MODEL, message, details);
    }

    /**
     * 任意の不変条件違反を表す例外を生成します。
     *
     * @param target 問題対象
     * @param rejectedValue 問題となった値
     * @param reason 不正理由
     * @return 生成した例外
     */
    public static InvalidModelException invalid(
            final String target,
            final Object rejectedValue,
            final String reason
    ) {
        return new InvalidModelException(
                message(target, reason),
                details(target, reason, rejectedValue)
        );
    }
}
