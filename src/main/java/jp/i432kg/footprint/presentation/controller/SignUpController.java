package jp.i432kg.footprint.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jp.i432kg.footprint.domain.value.Authority;
import jp.i432kg.footprint.infrastructure.datasource.UserDetailsServiceImpl;
import jp.i432kg.footprint.presentation.form.SignupForm;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class SignUpController {

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @GetMapping("/signup")
    public String newSignup(final SignupForm signupForm) {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(
            @Validated final SignupForm signupForm,
            final BindingResult result,
            final Model model,
            final HttpServletRequest request) {

        if (result.hasErrors()) {
            return "signup";
        }

        if (userDetailsServiceImpl.isExistUser(signupForm.getUsername())) {
            model.addAttribute("signupError", "ユーザー名 " + signupForm.getUsername() + "は既に登録されています");
            return "signup";
        }

        try {
            userDetailsServiceImpl.register(signupForm.toUserName(), signupForm.toRawPassword(), Authority.GENERAL);
        } catch (DataAccessException e) {
            model.addAttribute("signupError", "ユーザー登録に失敗しました");
            return "signup";
        }

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken == false) {
            SecurityContextHolder.clearContext();
        }

        try {
            request.login(signupForm.getUsername(), signupForm.getPassword());
        } catch (ServletException e) {
            e.printStackTrace();
        }

        return "redirect:/";
    }
}
