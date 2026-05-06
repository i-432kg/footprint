package jp.i432kg.footprint.logging.access;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jp.i432kg.footprint.infrastructure.security.UserDetailsImpl;
import jp.i432kg.footprint.logging.LoggingCategories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggingEventBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * HTTP リクエスト単位の access ログを記録する Filter です。
 */
@Component
public class AccessLogFilter extends OncePerRequestFilter {

    private static final Logger ACCESS_LOGGER = LoggerFactory.getLogger(LoggingCategories.ACCESS);
    private static final int INTERNAL_SERVER_ERROR = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

    //他の Filter / Interceptor と名前衝突しないよう、FQCN を接頭辞に使います。
    private static final String CONTEXT_ATTRIBUTE = AccessLogFilter.class.getName() + ".context";

    /**
     * 現在の HTTP リクエストに access ログ用のイベント名を設定します。
     *
     * @param request event を保持する HTTP リクエスト
     * @param event access ログへ出力するイベント名
     */
    public static void setEvent(final HttpServletRequest request, final String event) {
        getOrCreateContext(request).setEvent(event);
    }

    /**
     * 現在の HTTP リクエストに failure / warning 系 event 解決用の operation 名を設定します。
     *
     * @param request operation を保持する HTTP リクエスト
     * @param operation リクエストが表す operation 名
     */
    public static void setOperation(final HttpServletRequest request, final String operation) {
        getOrCreateContext(request).setOperation(operation);
    }

    /**
     * 現在の HTTP リクエストに access ログ用の追加項目を登録します。
     *
     * <p>controller 側で設定した値は、レスポンス完了時に 1 本の access ログへ集約されます。
     *
     * @param request 追加項目を保持する HTTP リクエスト
     * @param fieldName 追加するログ項目名
     * @param fieldValue 追加するログ項目値
     */
    public static void addField(
            final HttpServletRequest request,
            final String fieldName,
            final Object fieldValue
    ) {
        getOrCreateContext(request).addField(fieldName, fieldValue);
    }

    /**
     * 現在の HTTP リクエストに紐づく access ログコンテキストを取得します。
     *
     * @param request access ログコンテキストを参照したい HTTP リクエスト
     * @return access ログコンテキスト。未設定時は空
     */
    public static Optional<AccessLogContext> findContext(final HttpServletRequest request) {
        final Object attribute = request.getAttribute(CONTEXT_ATTRIBUTE);
        return attribute instanceof AccessLogContext context ? Optional.of(context) : Optional.empty();
    }

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
        final AccessLogContext context = findContext(request).orElseGet(AccessLogContext::new);
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final LoggingEventBuilder builder = ACCESS_LOGGER.atInfo()
                .addKeyValue("event", context.event())
                .addKeyValue("method", request.getMethod())
                .addKeyValue("path", request.getRequestURI())
                .addKeyValue("status", status)
                .addKeyValue("durationMs", durationMs);

        appendFields(builder, context.fields());

        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            builder.addKeyValue("userId", userDetails.getUserId().getValue())
                    .addKeyValue("username", userDetails.getDisplayUsername());
        }

        builder.log("HTTP access completed");
    }

    /**
     * request 固有の追加ログ項目を fluent logging の key-value として連結します。
     *
     * @param builder ログ出力ビルダー
     * @param fields 追加ログ項目
     */
    private void appendFields(final LoggingEventBuilder builder, final Map<String, Object> fields) {
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            builder.addKeyValue(entry.getKey(), entry.getValue());
        }
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

    /**
     * request に紐づく access ログコンテキストを取得し、未作成なら初期化します。
     *
     * @param request access ログコンテキストを保持する HTTP リクエスト
     * @return access ログコンテキスト
     */
    private static AccessLogContext getOrCreateContext(final HttpServletRequest request) {
        return findContext(request).orElseGet(() -> {
            final AccessLogContext newContext = new AccessLogContext();
            request.setAttribute(CONTEXT_ATTRIBUTE, newContext);
            return newContext;
        });
    }
}
