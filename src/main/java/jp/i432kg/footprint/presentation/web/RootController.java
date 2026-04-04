package jp.i432kg.footprint.presentation.web;

import jp.i432kg.footprint.infrastructure.security.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Objects;

/**
 * 画面に関する処理を行うコントローラー
 */
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
            model.addAttribute("displayUsername", userDetails.getDisplayUsername());
        }
    }

    /**
     * トップページにリダイレクトします。
     *
     * @return トップページ（タイムライン画面）
     */
    @GetMapping("/")
    public String index() {
        return "timeline";
    }

    /**
     * ログイン画面にリダイレクトします。
     *
     * @return ログイン画面
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 投稿マップ画面にリダイレクトします。
     *
     * @return 投稿マップ画面
     */
    @GetMapping("/map")
    public String map() {
        return "map";
    }

    /**
     * マイページ画面にリダイレクトします。
     *
     * @return マイページ画面
     */
    @GetMapping("/mypage")
    public String mypage() {
        return "mypage";
    }

    /**
     * 検索画面にリダイレクトします。
     *
     * @return 検索画面
     */
    @GetMapping("/search")
    public String search() {
        return "search";
    }

    /**
     * タイムライン画面にリダイレクトします。
     *
     * @return タイムライン画面
     */
    @GetMapping("/timeline")
    public String timeline() {
        return "timeline";
    }
}
