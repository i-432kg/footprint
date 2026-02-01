package jp.i432kg.footprint.presentation.api;

import jakarta.servlet.http.HttpServletRequest;
import jp.i432kg.footprint.application.service.UserService;
import jp.i432kg.footprint.domain.value.Authority;
import jp.i432kg.footprint.domain.value.BirthDate;
import jp.i432kg.footprint.domain.value.RawPassword;
import jp.i432kg.footprint.domain.value.UserName;
import jp.i432kg.footprint.presentation.api.dto.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SignUpRestController {

    private final UserService userService;

    @PostMapping("/api/signup")
    public ResponseEntity<?> signup(
            @Validated @RequestBody final SignUpRequest signUpRequest,
            BindingResult result,
            HttpServletRequest request) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        final UserName userName = UserName.of(signUpRequest.getUsername());
        final RawPassword rawPassword = RawPassword.of(signUpRequest.getPassword());
        final BirthDate birthDate = BirthDate.of(signUpRequest.getBirthDate());

        try {
            userService.register(userName, rawPassword, Authority.GENERAL, birthDate);

            // 登録後、そのままログイン状態にする
            request.login(signUpRequest.getUsername(), signUpRequest.getPassword());

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("ユーザー登録に失敗しました");
        }
    }

}
