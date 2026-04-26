package jp.i432kg.footprint.logging.access;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jp.i432kg.footprint.infrastructure.security.UserDetailsImpl;
import jp.i432kg.footprint.logging.LoggingCategories;
import jp.i432kg.footprint.logging.LoggingEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * HTTP リクエスト単位の access ログを記録する Filter です。
 */
@Component
public class AccessLogFilter extends OncePerRequestFilter {

    private static final Logger ACCESS_LOGGER = LoggerFactory.getLogger(LoggingCategories.ACCESS);
    private static final int INTERNAL_SERVER_ERROR = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

    /**
     * 1 リクエスト分の処理時間を計測し、完了後に access ログを出力します。
     *
     * <p>下流で例外が送出された場合でも access ログを欠落させないため、
     * 例外を補足してステータスを補正したうえで記録し、元の例外は再送出します。
     *
     * @param request 現在処理中の HTTP リクエスト
     * @param response 現在処理中の HTTP レスポンス
     * @param filterChain 後続の Filter / Controller 呼び出し
     * @throws ServletException Servlet 処理中に発生した例外
     * @throws IOException 入出力処理中に発生した例外
     */
    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        final long startNanos = System.nanoTime();

        try {
            filterChain.doFilter(request, response);
        } catch (ServletException | IOException | RuntimeException ex) {
            logAccess(request, resolveStatusForException(response), elapsedMillis(startNanos));
            throw ex;
        }

        logAccess(request, response.getStatus(), elapsedMillis(startNanos));
    }

    /**
     * リクエスト情報を `footprint.access` カテゴリへ記録します。
     *
     * <p>認証済みユーザーが `SecurityContext` に存在する場合は、
     * 障害調査やアクセス分析に使えるよう `userId` と `username` も追加します。
     * 未認証アクセスや匿名アクセスでは、ユーザー情報なしの最小セットだけを出力します。
     *
     * @param request ログ対象の HTTP リクエスト
     * @param status レスポンスとして扱う HTTP ステータス
     * @param durationMs リクエスト処理に要した時間（ミリ秒）
     */
    private void logAccess(final HttpServletRequest request, final int status, final long durationMs) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            ACCESS_LOGGER.info(
                    "event={}, method={}, path={}, status={}, durationMs={}, userId={}, username={}",
                    LoggingEvents.HTTP_ACCESS,
                    request.getMethod(),
                    request.getRequestURI(),
                    status,
                    durationMs,
                    userDetails.getUserId().getValue(),
                    userDetails.getDisplayUsername()
            );
            return;
        }

        ACCESS_LOGGER.info(
                "event={}, method={}, path={}, status={}, durationMs={}",
                LoggingEvents.HTTP_ACCESS,
                request.getMethod(),
                request.getRequestURI(),
                status,
                durationMs
        );
    }

    /**
     * 例外送出時に access ログへ記録するステータスコードを決定します。
     *
     * <p>`GlobalExceptionHandler` などがすでに 4xx / 5xx を設定済みならその値を採用し、
     * まだエラー系ステータスが入っていない未処理例外は 500 として扱います。
     *
     * @param response 例外発生時点の HTTP レスポンス
     * @return access ログに記録する HTTP ステータス
     */
    private int resolveStatusForException(final HttpServletResponse response) {
        return response.getStatus() >= 400 ? response.getStatus() : INTERNAL_SERVER_ERROR;
    }

    /**
     * 開始時刻から現在までの経過時間をミリ秒へ変換します。
     *
     * @param startNanos `System.nanoTime()` で取得した処理開始時刻
     * @return 経過時間（ミリ秒）
     */
    private long elapsedMillis(final long startNanos) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
    }
}
