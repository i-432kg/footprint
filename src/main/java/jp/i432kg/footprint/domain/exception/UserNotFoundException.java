package jp.i432kg.footprint.domain.exception;

public class UserNotFoundException extends DomainException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
