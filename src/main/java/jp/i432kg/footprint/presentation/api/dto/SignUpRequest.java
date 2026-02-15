package jp.i432kg.footprint.presentation.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SignUpRequest {

    @NotBlank
    private String loginId;

    @NotBlank
    private String password;

//    @NotBlank
//    private String userName;

    @NotNull
    private LocalDate birthDate;

}
