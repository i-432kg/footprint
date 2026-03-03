package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.value.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * ユーザードメインモデル
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class User {

    /**
     * ユーザー名
     */
    UserName userName;

    /**
     * メールアドレス（ログインID）
     */
    EmailAddress email;

    /**
     * 暗号化されたパスワード
     */
    HashedPassword hashedPassword;

    /**
     * 生年月日
     */
    BirthDate birthDate;

    /**
     * ユーザードメインモデルを生成します。
     *
     * @param userName       ユーザー名
     * @param email          メールアドレス
     * @param hashedPassword 暗号化されたパスワード
     * @param birthDate      生年月日
     * @return {@link User} インスタンス
     */
    public static User of(
            final UserName userName,
            final EmailAddress email,
            final HashedPassword hashedPassword,
            final BirthDate birthDate
    ) {
        return new User(userName, email, hashedPassword, birthDate);
    }
}
