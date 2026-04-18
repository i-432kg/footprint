package jp.i432kg.footprint.presentation.web;

import jp.i432kg.footprint.infrastructure.security.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Objects;

/**
 * 画面遷移用の Web エンドポイントを提供する controller です。
 * <p>
 * 認証済みユーザーの表示名を共通モデルへ追加し、各画面テンプレート名を返します。
 */
@Controller
public class RootController {

    /**
     * 認証済みユーザーが存在する場合に、表示名を共通モデル属性へ追加します。
     *
     * @param model ビュー描画に利用する model
     * @param userDetails 認証済みユーザー。未認証時は {@code null}
     */
    @ModelAttribute
    public void addLoginUserToModel(final Model model, @AuthenticationPrincipal final UserDetailsImpl userDetails) {
        if (Objects.nonNull(userDetails)) {
            model.addAttribute("displayUsername", userDetails.getDisplayUsername());
        }
    }

    /**
     * ルートパスに対してタイムライン画面を返します。
     *
     * @return タイムライン画面のテンプレート名
     */
    @GetMapping("/")
    public String index() {
        return "timeline";
    }

    /**
     * ログイン画面を返します。
     *
     * @return ログイン画面のテンプレート名
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 投稿マップ画面を返します。
     *
     * @return 投稿マップ画面のテンプレート名
     */
    @GetMapping("/map")
    public String map() {
        return "map";
    }

    /**
     * マイページ画面を返します。
     *
     * @return マイページ画面のテンプレート名
     */
    @GetMapping("/mypage")
    public String mypage() {
        return "mypage";
    }

    /**
     * 検索画面を返します。
     *
     * @return 検索画面のテンプレート名
     */
    @GetMapping("/search")
    public String search() {
        return "search";
    }

    /**
     * タイムライン画面を返します。
     *
     * @return タイムライン画面のテンプレート名
     */
    @GetMapping("/timeline")
    public String timeline() {
        return "timeline";
    }
}
