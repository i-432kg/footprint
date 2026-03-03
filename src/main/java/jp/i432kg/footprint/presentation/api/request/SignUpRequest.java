package jp.i432kg.footprint.presentation.api.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * ユーザー登録リクエスト
 */
@Getter
@Setter
public class SignUpRequest {

    /**
     * ユーザー名
     */
    private String userName;

    /**
     * メールアドレス（ログインID）
     */
    private String email;

    /**
     * ハッシュ化前のパスワード
     */
    private String password;

    /**
     * 生年月日
     */
    private LocalDate birthDate;

}
