package jp.i432kg.footprint.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * API の認証失敗時の応答制御を担当する {@link AuthenticationFailureHandler} 実装です。
 *
 * <p>認証失敗イベントの記録と API 向けエラーレスポンス生成を受け持ちます。
 */
@Slf4j
@Component
public class ApiAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final String EVENT = "AUTH_LOGIN_FAILURE";
    private static final String REASON = "INVALID_CREDENTIALS";

    @Override
    public void onAuthenticationFailure(
            final @NonNull HttpServletRequest request,
            final @NonNull HttpServletResponse response,
            final @NonNull AuthenticationException exception
    ) throws IOException {
        log.warn("event={}, reason={}, method={}, path={}",
                EVENT, REASON, request.getMethod(), request.getRequestURI());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed");
    }
}
