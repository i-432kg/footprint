package jp.i432kg.footprint.domain.exception;

import jp.i432kg.footprint.exception.DetailBasedException;
import jp.i432kg.footprint.exception.ErrorCode;

import java.util.Map;

/**
 * ドメイン層で発生する例外の基底クラス
 */
public abstract class DomainException extends DetailBasedException {

    protected DomainException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }

    protected DomainException(ErrorCode errorCode, String message, Map<String, Object> details, Throwable cause) {
        super(errorCode, message, details, cause);
    }
}