package jp.i432kg.footprint.domain.exception;

/**
 * ドメイン層で発生する例外の基底クラス
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}