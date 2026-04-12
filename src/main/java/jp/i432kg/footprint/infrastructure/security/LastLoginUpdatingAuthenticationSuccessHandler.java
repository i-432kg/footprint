package jp.i432kg.footprint.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * 認証成功時に最終ログイン日時を更新するハンドラです。
 */
@Component
@RequiredArgsConstructor
public class LastLoginUpdatingAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final LastLoginRecorder lastLoginRecorder;

    @Override
    public void onAuthenticationSuccess(
            final @NonNull HttpServletRequest request,
            final @NonNull HttpServletResponse response,
            final Authentication authentication
    ) {
        if (authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            lastLoginRecorder.recordSuccessfulLogin(userDetails.getUserId());
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
