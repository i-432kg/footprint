package jp.i432kg.footprint.domain.exception;

/**
 * 値オブジェクトのバリデーションエラー（ドメインの不変条件違反）を示す例外
 */
public class InvalidValueException extends DomainException {

    private final String messageKey;
    private final Object[] args;

    public InvalidValueException(String messageKey, Object... args) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object[] getArgs() {
        return args;
    }
}