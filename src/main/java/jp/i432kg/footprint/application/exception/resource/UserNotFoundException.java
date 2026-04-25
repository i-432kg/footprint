package jp.i432kg.footprint.application.exception.resource;

import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.exception.ErrorCode;

/**
 * 指定されたユーザーが存在しない場合にスローされる例外です。
 */
public class UserNotFoundException extends ResourceNotFoundException {

    public UserNotFoundException(UserId userId) {
        super(
                ErrorCode.USER_NOT_FOUND,
                "User not found. userId=" + userId.getValue(),
                details("user", userId.getValue())
        );
    }
}
