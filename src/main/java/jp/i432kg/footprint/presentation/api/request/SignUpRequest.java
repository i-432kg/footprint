package jp.i432kg.footprint.presentation.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jp.i432kg.footprint.presentation.validation.PresentationValidationPatterns;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * サインアップ API へ渡す JSON リクエスト DTO です。
 * <p>
 * ユーザー名、メールアドレス、パスワード、生年月日の入力制約を Bean Validation で定義します。
 */
@Getter
@Setter
public class SignUpRequest {

    /**
     * ユーザー名です。
     * <p>
     * 4 文字以上 15 文字以下で、空白を含まない ASCII 可視文字のみを受け付けます。
     */
    @NotBlank
    @Size(min = 4, max = 15)
    @Pattern(regexp = PresentationValidationPatterns.ASCII_VISIBLE_NO_SPACE)
    private String userName;

    /**
     * ログイン ID として利用するメールアドレスです。
     * <p>
     * 必須項目であり、254 文字以内かつメールアドレス形式である必要があります。
     */
    @NotBlank
    @Size(max = 254)
    @Email
    private String email;

    /**
     * ハッシュ化前のパスワードです。
     * <p>
     * 8 文字以上 72 文字以下で、空白を含まない ASCII 可視文字のみを受け付けます。
     */
    @NotBlank
    @Size(min = 8, max = 72)
    @Pattern(regexp = PresentationValidationPatterns.ASCII_VISIBLE_NO_SPACE)
    private String password;

    /**
     * 生年月日です。
     * <p>
     * 必須項目であり、過去日または当日である必要があります。
     */
    @NotNull
    @PastOrPresent
    private LocalDate birthDate;

}
