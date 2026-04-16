package jp.i432kg.footprint.domain.exception;

import jp.i432kg.footprint.exception.ErrorCode;

import java.util.Map;

/**
 * ドメインモデル全体の不変条件違反を示す例外
 */
public class InvalidModelException extends DomainException {

    private InvalidModelException(String message, Map<String, Object> details) {
        super(ErrorCode.DOMAIN_INVALID_MODEL, message, details);
    }

    public static InvalidModelException invalid(String target, Object rejectedValue, String reason) {
        return new InvalidModelException(
                message(target, reason),
                details(target, reason, rejectedValue)
        );
    }
}
