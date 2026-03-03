package jp.i432kg.footprint.application.command.model;

import jp.i432kg.footprint.domain.value.BirthDate;
import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.domain.value.RawPassword;
import jp.i432kg.footprint.domain.value.UserName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * ユーザー作成を指示するコマンドオブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateUserCommand {

    /**
     * ユーザー名
     */
    UserName userName;

    /**
     * メールアドレス（ログインID）
     */
    EmailAddress email;

    /**
     * ハッシュ化前のパスワード
     */
    RawPassword rawPassword;

    /**
     * 生年月日
     */
    BirthDate birthDate;

    /**
     * {@link CreateUserCommand} インスタンスを生成します。
     *
     * @param userName    ユーザー名
     * @param email       メールアドレス
     * @param rawPassword ハッシュ化前のパスワード
     * @param birthDate   生年月日
     * @return {@link CreateUserCommand} インスタンス
     */
    public static CreateUserCommand of(
            final UserName userName,
            final EmailAddress email,
            final RawPassword rawPassword,
            final BirthDate birthDate
    ) {
        return new CreateUserCommand(userName, email, rawPassword, birthDate);
    }
}
