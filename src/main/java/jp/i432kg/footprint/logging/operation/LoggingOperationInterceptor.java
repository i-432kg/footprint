package jp.i432kg.footprint.logging.operation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jp.i432kg.footprint.logging.access.AccessLogFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * handler method に宣言された operation 名を request 文脈へ設定する interceptor です。
 */
@Component
public class LoggingOperationInterceptor implements HandlerInterceptor {

    /**
     * handler method に {@link LogOperation} が付いている場合、request 文脈へ operation を設定します。
     *
     * @return 後続処理を継続するため常に {@code true}
     */
    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) {
        // controller method 以外の handler は operation 設定対象外なので、そのまま後続へ流す
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // method に宣言された @LogOperation を取得する
        final LogOperation annotation = handlerMethod.getMethodAnnotation(LogOperation.class);
        // annotation がない endpoint では operation を設定せず、そのまま後続へ流す
        if (annotation == null) {
            return true;
        }

        // request 文脈へ operation 名を設定し、後続の access / app ログ解決で参照できるようにする
        AccessLogFilter.setOperation(request, annotation.value());
        return true;
    }
}
