package jp.i432kg.footprint.config;

import jakarta.servlet.http.HttpServletResponse;
import jp.i432kg.footprint.infrastructure.security.LastLoginUpdatingAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.HttpStatusAccessDeniedHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final Environment environment;
    private final StorageSecurityProperties storageSecurityProperties;
    private final LastLoginUpdatingAuthenticationSuccessHandler authenticationSuccessHandler;

    public SecurityConfig(
            final Environment environment,
            final StorageSecurityProperties storageSecurityProperties,
            final LastLoginUpdatingAuthenticationSuccessHandler authenticationSuccessHandler
    ) {
        this.environment = environment;
        this.storageSecurityProperties = storageSecurityProperties;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                // セッション認証を継続しつつ、SPA/JS から扱いやすい CSRF にする
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
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
                            "/signup",
                            "/favicon.ico",
                            "/favicon.svg",
                            "/css/**",
                            "/assets/**",
                            "/images/**",
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

                    auth.requestMatchers(HttpMethod.GET, "/api/posts").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/posts/search").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/posts/search/map").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/posts/*").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/posts/*/replies").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/replies/*").permitAll();

                    // 認証必須 API
                    auth.requestMatchers("/api/users/me/**").authenticated();
                    auth.requestMatchers(HttpMethod.POST, "/api/posts").authenticated();
                    auth.requestMatchers(HttpMethod.POST, "/api/replies/**").authenticated();

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
                        .failureHandler(
                                (request, response, exception) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed")
                        )
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
                                (request, response, authException) ->
                                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED),
                                PathPatternRequestMatcher.withDefaults().matcher("/api/**")
                        )
                        .defaultAccessDeniedHandlerFor(
                                new HttpStatusAccessDeniedHandler(HttpStatus.FORBIDDEN),
                                PathPatternRequestMatcher.withDefaults().matcher("/api/**")
                        )
                )

                .securityContext(Customizer.withDefaults());

        return http.build();
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
