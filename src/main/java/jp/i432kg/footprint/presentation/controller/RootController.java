package jp.i432kg.footprint.presentation.controller;

import jp.i432kg.footprint.infrastructure.datasource.impl.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Objects;

@Controller
public class RootController {

    /**
     * ログインユーザーの情報をモデルに追加します。
     *
     * @param model       モデルオブジェクト
     * @param userDetails 認証されたユーザーの詳細情報
     */
    @ModelAttribute
    public void addLoginUserToModel(final Model model, @AuthenticationPrincipal final UserDetailsImpl userDetails) {
        if (Objects.nonNull(userDetails)) {
            model.addAttribute("username", userDetails.getUsername());
        }
    }

    @GetMapping("/")
    public String index() {
        return "timeline";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/map")
    public String map() {
        return "map";
    }

    @GetMapping("/mypage")
    public String mypage() {
        return "mypage";
    }

    @GetMapping("/search")
    public String search() {
        return "search";
    }

    @GetMapping("/timeline")
    public String timeline() {
        return "timeline";
    }
}
