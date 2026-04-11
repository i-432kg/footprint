package jp.i432kg.footprint.infrastructure.security;

import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.infrastructure.security.mapper.AuthMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 認証成功時の最終ログイン日時を記録するサービスです。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LastLoginRecorder {

    private final AuthMapper authMapper;

    public void recordSuccessfulLogin(final UserId userId) {
        try {
            authMapper.updateLastLoginAt(userId, LocalDateTime.now());
        } catch (RuntimeException e) {
            // 認証自体は成功しているため、監査用更新失敗はログに残して処理は継続する。
            log.warn("Failed to update last login timestamp after authentication success. userId={}", userId.getValue(), e);
        }
    }
}
