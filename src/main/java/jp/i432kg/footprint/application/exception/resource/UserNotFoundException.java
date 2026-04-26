package jp.i432kg.footprint.application.exception.resource;

import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.exception.ErrorCode;

/**
 * 指定されたユーザーが存在しないことを表す例外です。
 */
public class UserNotFoundException extends ResourceNotFoundException {

    /**
     * ユーザー未検出例外を生成します。
     *
     * @param userId 未検出となったユーザー ID
     */
    public UserNotFoundException(final UserId userId) {
        super(
                ErrorCode.USER_NOT_FOUND,
                "User not found. userId=" + userId.getValue(),
                details("user", userId.getValue())
        );
    }
}
