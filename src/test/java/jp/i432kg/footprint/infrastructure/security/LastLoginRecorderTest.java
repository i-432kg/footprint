package jp.i432kg.footprint.infrastructure.security;

import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.infrastructure.security.mapper.AuthMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LastLoginRecorderTest {

    @Mock
    private AuthMapper authMapper;

    @Test
    void recordSuccessfulLogin_shouldUpdateLastLoginAt() {
        final var recorder = new LastLoginRecorder(authMapper);
        final UserId userId = UserId.of("01JQW8D4Q3G9Y2X6N7M8P9R0ST");

        recorder.recordSuccessfulLogin(userId);

        verify(authMapper).updateLastLoginAt(eq(userId), any(LocalDateTime.class));
    }
}
