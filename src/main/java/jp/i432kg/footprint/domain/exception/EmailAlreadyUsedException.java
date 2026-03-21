package jp.i432kg.footprint.domain.exception;

import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.exception.ErrorCode;

import java.util.Map;

public class EmailAlreadyUsedException extends DomainException {

    public EmailAlreadyUsedException(EmailAddress emailAddress) {
        super(
                ErrorCode.EMAIL_ALREADY_USED,
                "this email address is already used",
                Map.of(
                        "emailAddress", emailAddress
                )
        );
    }
}
