package jp.i432kg.footprint.presentation.api;

import jp.i432kg.footprint.infrastructure.datasource.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthRestController {

    /**
     * 現在のログインユーザー情報をJSONで返す
     */
    @GetMapping("/api/me")
    public MeResponse getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails != null) {
            return new MeResponse(userDetails.getUsername(), true);
        }
        return new MeResponse("ゲスト", false);
    }

    public record MeResponse(String name, boolean authenticated) {
    }
}
