package jp.i432kg.footprint.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * API の認可失敗時の応答制御を担当する {@link AccessDeniedHandler} 実装です。
 *
 * <p>認可エラーや CSRF 拒否を API 向けの拒否応答へ変換し、関連イベントを記録します。
 */
@Slf4j
@Component
public class ApiAccessDeniedHandler implements AccessDeniedHandler {

    private static final String EVENT_FORBIDDEN = "AUTH_FORBIDDEN";
    private static final String EVENT_CSRF_REJECTED = "AUTH_CSRF_REJECTED";

    @Override
    public void handle(
            final @NonNull HttpServletRequest request,
            final @NonNull HttpServletResponse response,
            final @NonNull AccessDeniedException accessDeniedException
    ) throws IOException {

        final String event = isCsrfException(accessDeniedException) ?
                EVENT_CSRF_REJECTED :
                EVENT_FORBIDDEN;
        log.warn("event={}, method={}, path={}", event, request.getMethod(), request.getRequestURI());

        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    private boolean isCsrfException(final @NonNull AccessDeniedException accessDeniedException) {
        return accessDeniedException instanceof CsrfException;
    }
}
