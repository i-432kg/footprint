package jp.i432kg.footprint.config;

import jakarta.servlet.http.HttpServletResponse;
import jp.i432kg.footprint.infrastructure.security.ApiAccessDeniedHandler;
import jp.i432kg.footprint.infrastructure.security.ApiAuthenticationEntryPoint;
import jp.i432kg.footprint.infrastructure.security.ApiAuthenticationFailureHandler;
import jp.i432kg.footprint.infrastructure.security.ApiAuthenticationSuccessHandler;
import jp.i432kg.footprint.logging.access.AccessLogFilter;
import jp.i432kg.footprint.logging.trace.TraceIdFilter;
import org.springframework.boot.web.server.servlet.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;
import java.util.function.Consumer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final Environment environment;
    private final StorageSecurityProperties storageSecurityProperties;
    private final ApiAuthenticationSuccessHandler authenticationSuccessHandler;
    private final ApiAuthenticationFailureHandler authenticationFailureHandler;
    private final ApiAuthenticationEntryPoint authenticationEntryPoint;
    private final ApiAccessDeniedHandler accessDeniedHandler;
    private final TraceIdFilter traceIdFilter;
    private final AccessLogFilter accessLogFilter;

    public SecurityConfig(
            final Environment environment,
            final StorageSecurityProperties storageSecurityProperties,
            final ApiAuthenticationSuccessHandler authenticationSuccessHandler,
            final ApiAuthenticationFailureHandler authenticationFailureHandler,
            final ApiAuthenticationEntryPoint authenticationEntryPoint,
            final ApiAccessDeniedHandler accessDeniedHandler,
            final TraceIdFilter traceIdFilter,
            final AccessLogFilter accessLogFilter
    ) {
        this.environment = environment;
        this.storageSecurityProperties = storageSecurityProperties;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.traceIdFilter = traceIdFilter;
        this.accessLogFilter = accessLogFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CookieSameSiteSupplier csrfCookieSameSiteSupplier() {
        return CookieSameSiteSupplier.ofLax().whenHasName("XSRF-TOKEN");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        final CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        csrfTokenRepository.setCookieCustomizer(buildCsrfCookieCustomizer());

        http
                // セッション認証を継続しつつ、SPA/JS から扱いやすい CSRF にする
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository)
                        .spa()
                        .ignoringRequestMatchers("/actuator/health")
                )

                // Spring Security のデフォルトヘッダは活かし、必要なものだけ追加
                .headers(headers -> {
                    headers.referrerPolicy(referrer -> referrer
                            .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                    );

                    // ローカル起動時は CSP 無効
                    if (!isLocalProfile()) {
                        headers.contentSecurityPolicy(csp -> csp.policyDirectives(buildContentSecurityPolicy()));
                    }
                })

                .authorizeHttpRequests(auth -> {
                    // 画面・静的リソース
                    auth.requestMatchers(
                            "/login",
                            "/about",
                            "/terms",
                            "/privacy",
                            "/favicon.ico",
                            "/favicon.svg",
                            "/footprint-title-logo.svg",
                            "/css/**",
                            "/assets/**",
                            "/actuator/health"
                    ).permitAll();

                    // ローカル起動時のみ Open API を許可
                    if (isLocalProfile()) {
                        auth.requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml"
                        ).permitAll();
                    }

                    // 認証不要 API
                    auth.requestMatchers(HttpMethod.POST, "/api/login").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/api/users").permitAll();

                    // その他の API は閉じる
                    auth.requestMatchers("/api/**").authenticated();

                    // それ以外の画面も認証必須
                    auth.anyRequest().authenticated();
                })

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/api/login")
                        .usernameParameter("loginId")
                        .successHandler(authenticationSuccessHandler)
                        .failureHandler(authenticationFailureHandler)
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutSuccessHandler(
                                (request, response, authentication) ->
                                response.setStatus(HttpServletResponse.SC_NO_CONTENT)
                        )
                )

                .exceptionHandling(ex -> ex
                        .defaultAuthenticationEntryPointFor(
                                authenticationEntryPoint,
                                PathPatternRequestMatcher.withDefaults().matcher("/api/**")
                        )
                        .defaultAccessDeniedHandlerFor(
                                accessDeniedHandler,
                                PathPatternRequestMatcher.withDefaults().matcher("/api/**")
                        )
                )

                .securityContext(Customizer.withDefaults());

        http.addFilterBefore(traceIdFilter, SecurityContextHolderFilter.class);
        http.addFilterAfter(accessLogFilter, SecurityContextHolderFilter.class);

        return http.build();
    }

    private Consumer<ResponseCookie.ResponseCookieBuilder> buildCsrfCookieCustomizer() {
        return builder -> builder
                .sameSite("Lax")
                .secure(!isLocalProfile());
    }

    private boolean isLocalProfile() {
        return Arrays.stream(environment.getActiveProfiles())
                .anyMatch(profile -> "local".equals(profile) || "dev".equals(profile));
    }

    private String buildContentSecurityPolicy() {
        final String additionalImageOrigins = String.join(" ", storageSecurityProperties.getImageCspAllowOriginList());
        final String imageSrc = additionalImageOrigins.isBlank()
                ? "'self' data: blob:"
                : "'self' data: blob: " + additionalImageOrigins;

        return "default-src 'self'; " +
                "img-src " + imageSrc + "; " +
                "style-src 'self' 'unsafe-inline'; " +
                "script-src 'self'; " +
                "object-src 'none'; " +
                "base-uri 'self'; " +
                "frame-ancestors 'self'; " +
                "form-action 'self'";
    }
}
