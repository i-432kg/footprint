package jp.i432kg.footprint.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jp.i432kg.footprint.logging.LoggingCategories;
import jp.i432kg.footprint.logging.LoggingEvents;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * API の未認証アクセス時の開始点を担当する {@link AuthenticationEntryPoint} 実装です。
 *
 * <p>認証が必要なリクエストを API 向けの未認証エラー応答へ変換します。
 */
@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger AUTH_LOGGER = LoggerFactory.getLogger(LoggingCategories.AUTH);

    @Override
    public void commence(
            final @NonNull HttpServletRequest request,
            final @NonNull HttpServletResponse response,
            final @NonNull AuthenticationException authException
    ) throws IOException {
        AUTH_LOGGER.atWarn()
                .addKeyValue("event", LoggingEvents.AUTH_UNAUTHORIZED)
                .addKeyValue("method", request.getMethod())
                .addKeyValue("path", request.getRequestURI())
                .log("Authentication is required");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
