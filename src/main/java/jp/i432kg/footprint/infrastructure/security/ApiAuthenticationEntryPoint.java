package jp.i432kg.footprint.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * API の未認証アクセス時の開始点を担当する {@link AuthenticationEntryPoint} 実装です。
 *
 * <p>認証が必要なリクエストを API 向けの未認証エラー応答へ変換します。
 */
@Slf4j
@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String EVENT = "AUTH_UNAUTHORIZED";

    @Override
    public void commence(
            final @NonNull HttpServletRequest request,
            final @NonNull HttpServletResponse response,
            final @NonNull AuthenticationException authException
    ) throws IOException {
        log.warn("event={}, method={}, path={}", EVENT, request.getMethod(), request.getRequestURI());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
