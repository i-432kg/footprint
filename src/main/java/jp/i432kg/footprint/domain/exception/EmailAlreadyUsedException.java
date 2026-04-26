package jp.i432kg.footprint.domain.exception;

import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.exception.ErrorCode;

/**
 * メールアドレスの一意制約違反を表す業務例外です。
 * <p>
 * サインアップなどで、既に使用済みのメールアドレスを新規利用しようとした場合に送出します。
 */
public class EmailAlreadyUsedException extends DomainException {

    /**
     * 既に使用済みのメールアドレスであることを表す例外を生成します。
     *
     * @param emailAddress 問題となったメールアドレス
     */
    public EmailAlreadyUsedException(final EmailAddress emailAddress) {
        super(
                ErrorCode.EMAIL_ALREADY_USED,
                "this email address is already used",
                details("email", "already_used", emailAddress)
        );
    }
}
