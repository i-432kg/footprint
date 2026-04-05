package jp.i432kg.footprint.config;

import jakarta.servlet.http.HttpServletResponse;
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

    public SecurityConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
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
                        headers.contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; " +
                                        "img-src 'self' data: blob:; " +
                                        "style-src 'self' 'unsafe-inline'; " +
                                        "script-src 'self'; " +
                                        "object-src 'none'; " +
                                        "base-uri 'self'; " +
                                        "frame-ancestors 'self'; " +
                                        "form-action 'self'"
                        ));
                    }
                })

                .authorizeHttpRequests(auth -> auth
                        // 画面・静的リソース
                        .requestMatchers(
                                "/login",
                                "/signup",
                                "/favicon.ico",
                                "/css/**",
                                "/assets/**",
                                "/images/**",
                                "/actuator/health"
                        ).permitAll()

                        // 認証不要 API
                        .requestMatchers(HttpMethod.POST, "/api/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/posts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/search/map").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/*/replies").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/replies/*").permitAll()

                        // 認証必須 API
                        .requestMatchers("/api/users/me/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/posts").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/replies/**").authenticated()

                        // その他の API は閉じる
                        .requestMatchers("/api/**").authenticated()

                        // それ以外の画面も認証必須
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/api/login")
                        .usernameParameter("loginId")
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                        .failureHandler((request, response, exception) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed");
                        })
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                        })
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
}