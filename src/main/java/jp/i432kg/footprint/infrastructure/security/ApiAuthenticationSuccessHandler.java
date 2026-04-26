package jp.i432kg.footprint.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jp.i432kg.footprint.logging.LoggingCategories;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * API の認証成功後処理を担当する {@link AuthenticationSuccessHandler} 実装です。
 *
 * <p>認証成功時の副作用実行を入口として受け持ち、必要に応じて専用サービスへ委譲します。
 */
@Component
@RequiredArgsConstructor
public class ApiAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final String EVENT = "AUTH_LOGIN_SUCCESS";
    private static final Logger AUTH_LOGGER = LoggerFactory.getLogger(LoggingCategories.AUTH);

    private final LastLoginRecorder lastLoginRecorder;

    @Override
    public void onAuthenticationSuccess(
            final @NonNull HttpServletRequest request,
            final @NonNull HttpServletResponse response,
            final Authentication authentication
    ) {
        // 現状の通常ログインでは UserDetailsImpl を想定するが、認証方式変更やテスト入力でも成功フローは維持する
        if (authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            // ログイン日時を更新する
            lastLoginRecorder.recordSuccessfulLogin(userDetails.getUserId());

            AUTH_LOGGER.info("event={}, userId={}, username={}",
                    EVENT, userDetails.getUserId().getValue(), userDetails.getDisplayUsername());
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
