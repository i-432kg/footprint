package jp.i432kg.footprint.infrastructure.security;

import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.infrastructure.security.mapper.AuthMapper;
import jp.i432kg.footprint.logging.LoggingEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * 認証成功時の最終ログイン日時を記録するサービスです。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LastLoginRecorder {

    private final AuthMapper authMapper;
    private final Clock clock;

    public void recordSuccessfulLogin(final UserId userId) {
        try {
            authMapper.updateLastLoginAt(userId, LocalDateTime.now(clock));
        } catch (RuntimeException e) {
            // 認証自体は成功しているため、監査用更新失敗はログに残して処理は継続する。
            log.atWarn()
                    .addKeyValue("event", LoggingEvents.LAST_LOGIN_UPDATE_FAILED)
                    .addKeyValue("userId", userId.getValue())
                    .setCause(e)
                    .log("Failed to update last login timestamp");
        }
    }
}
