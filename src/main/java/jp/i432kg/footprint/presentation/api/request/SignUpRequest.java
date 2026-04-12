package jp.i432kg.footprint.presentation.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
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
    @Size(min = 4, max = 15)
    @Pattern(regexp = "^[\\x21-\\x7E]+$")
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
    @Pattern(regexp = "^[\\x21-\\x7E]+$")
    private String password;

    /**
     * 生年月日
     */
    @NotNull
    @Past
    private LocalDate birthDate;

}
