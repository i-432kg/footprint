package jp.i432kg.footprint.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jp.i432kg.footprint.logging.LoggingCategories;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * API の認証失敗時の応答制御を担当する {@link AuthenticationFailureHandler} 実装です。
 *
 * <p>認証失敗イベントの記録と API 向けエラーレスポンス生成を受け持ちます。
 */
@Component
public class ApiAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final String EVENT = "AUTH_LOGIN_FAILURE";
    private static final String REASON = "INVALID_CREDENTIALS";
    private static final Logger AUTH_LOGGER = LoggerFactory.getLogger(LoggingCategories.AUTH);

    @Override
    public void onAuthenticationFailure(
            final @NonNull HttpServletRequest request,
            final @NonNull HttpServletResponse response,
            final @NonNull AuthenticationException exception
    ) throws IOException {
        AUTH_LOGGER.warn("event={}, reason={}, method={}, path={}",
                EVENT, REASON, request.getMethod(), request.getRequestURI());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed");
    }
}
