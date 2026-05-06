package jp.i432kg.footprint.logging.trace;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * リクエスト単位の traceId を採番し、MDC とレスポンスヘッダへ設定する Filter です。
 */
@Component
public class TraceIdFilter extends OncePerRequestFilter {

    public static final String MDC_KEY = "traceId";
    public static final String HEADER_NAME = "X-Trace-Id";
    public static final String REQUEST_ATTRIBUTE = TraceIdFilter.class.getName() + ".traceId";

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        final String traceId = UlidCreator.getUlid().toString();

        request.setAttribute(REQUEST_ATTRIBUTE, traceId);
        response.setHeader(HEADER_NAME, traceId);
        MDC.put(MDC_KEY, traceId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }
}
