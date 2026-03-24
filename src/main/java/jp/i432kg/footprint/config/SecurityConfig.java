package jp.i432kg.footprint.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // CSS、画像パス、favicon.icoをセキュリティフィルタの対象外にする
        return (web) -> web.ignoring()
                .requestMatchers("/css/**")
                .requestMatchers("/assets/**")
                .requestMatchers("/images/**")
                .requestMatchers("/favicon.ico")
                .requestMatchers("/actuator/health");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // H2コンソール用にCSRF無効化
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // iframe許可
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/signup", "/login").permitAll()
                        .requestMatchers("/assets/**").permitAll()
                        .requestMatchers("/api/login").permitAll()
                        .requestMatchers("/api/**").permitAll() // 開発用にapiリクエスト許可
                        .requestMatchers("/images/**").permitAll() // 開発用に画像パスを許可
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/api/login")
                        .usernameParameter("loginId") // Spring Security のログイン識別子を loginId に変更する
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        }) // 成功時に200 OKを返す
                        .failureHandler((request, response, exception) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed");
                        }) // 失敗時に401 Unauthorizedを返す
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // ログアウト処理を実行するURL（デフォルトは/logout）
                        .logoutSuccessUrl("/login?logout") // ログアウト成功後の遷移先
                        .permitAll()
                );
        return http.build();
    }
}
