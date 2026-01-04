package jp.i432kg.footprint.presentation.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jp.i432kg.footprint.domain.value.RawPassword;
import jp.i432kg.footprint.domain.value.UserName;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignupForm {

    @NotBlank
    @Size(min = 1, max = 50)
    private String username;

    @NotBlank
    @Size(min = 6, max = 20)
    private String password;

    public UserName toUserName() {
        return new UserName(this.username);
    }

    public RawPassword toRawPassword() {
        return new RawPassword(this.password);
    }

}
