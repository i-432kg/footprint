package jp.i432kg.footprint.presentation.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
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
    @NotBlank
    @Size(max = 20)
    private String userName;

    /**
     * メールアドレス（ログインID）
     */
    @NotBlank
    @Size(max = 254)
    @Email
    private String email;

    /**
     * ハッシュ化前のパスワード
     */
    @NotBlank
    @Size(min = 8, max = 72)
    private String password;

    /**
     * 生年月日
     */
    @Past
    private LocalDate birthDate;

}
