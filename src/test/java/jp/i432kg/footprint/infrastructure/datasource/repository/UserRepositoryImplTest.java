package jp.i432kg.footprint.infrastructure.datasource.repository;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.model.User;
import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.infrastructure.datasource.mapper.repository.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-04-19T03:45:30Z"),
            ZoneId.of("Asia/Tokyo")
    );

    @Mock
    private UserMapper userMapper;

    @Test
    @DisplayName("UserRepositoryImpl.existsById はユーザーが存在する場合に true を返す")
    void should_returnTrue_when_userExists() {
        final UserId userId = DomainTestFixtures.userId();
        when(userMapper.countByUserId(userId)).thenReturn(1);

        final boolean actual = newRepository().existsById(userId);

        assertThat(actual).isTrue();
        verify(userMapper).countByUserId(userId);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    @DisplayName("UserRepositoryImpl.existsById はユーザーが存在しない場合に false を返す")
    void should_returnFalse_when_userDoesNotExist() {
        final UserId userId = DomainTestFixtures.userId();
        when(userMapper.countByUserId(userId)).thenReturn(0);

        final boolean actual = newRepository().existsById(userId);

        assertThat(actual).isFalse();
        verify(userMapper).countByUserId(userId);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    @DisplayName("UserRepositoryImpl.existsByEmail はメールアドレスが登録済みの場合に true を返す")
    void should_returnTrue_when_emailExists() {
        final EmailAddress email = DomainTestFixtures.user().getEmail();
        when(userMapper.countByEmail(email)).thenReturn(1);

        final boolean actual = newRepository().existsByEmail(email);

        assertThat(actual).isTrue();
        verify(userMapper).countByEmail(email);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    @DisplayName("UserRepositoryImpl.existsByEmail はメールアドレスが未登録の場合に false を返す")
    void should_returnFalse_when_emailDoesNotExist() {
        final EmailAddress email = DomainTestFixtures.user().getEmail();
        when(userMapper.countByEmail(email)).thenReturn(0);

        final boolean actual = newRepository().existsByEmail(email);

        assertThat(actual).isFalse();
        verify(userMapper).countByEmail(email);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    @DisplayName("UserRepositoryImpl.saveUser は固定 Clock に基づく時刻でユーザーを保存する")
    void should_saveUserWithClockTime_when_saveUserCalled() {
        final User user = DomainTestFixtures.user();

        newRepository().saveUser(user);

        final ArgumentCaptor<UserMapper.UserInsertEntity> captor =
                ArgumentCaptor.forClass(UserMapper.UserInsertEntity.class);
        verify(userMapper).insert(captor.capture());
        verifyNoMoreInteractions(userMapper);

        final UserMapper.UserInsertEntity actual = captor.getValue();
        final LocalDateTime expectedTime = LocalDateTime.now(FIXED_CLOCK);
        assertThat(actual.getId()).isNull();
        assertThat(actual.getUserId()).isEqualTo(user.getUserId());
        assertThat(actual.getUsername()).isEqualTo(user.getUserName());
        assertThat(actual.getEmail()).isEqualTo(user.getEmail());
        assertThat(actual.getPasswordHash()).isEqualTo(user.getHashedPassword());
        assertThat(actual.getBirthdate()).isEqualTo(user.getBirthDate());
        assertThat(actual.isActive()).isFalse();
        assertThat(actual.isDisabled()).isFalse();
        assertThat(actual.getDisabledAt()).isNull();
        assertThat(actual.getCreatedAt()).isEqualTo(expectedTime);
        assertThat(actual.getUpdatedAt()).isEqualTo(expectedTime);
    }

    @Test
    @DisplayName("UserRepositoryImpl.existsById は mapper 例外を再送出する")
    void should_rethrowException_when_existsByIdFails() {
        final UserId userId = DomainTestFixtures.userId();
        final RuntimeException expected = new RuntimeException("count by id failed");
        when(userMapper.countByUserId(userId)).thenThrow(expected);

        assertThatThrownBy(() -> newRepository().existsById(userId))
                .isSameAs(expected);

        verify(userMapper).countByUserId(userId);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    @DisplayName("UserRepositoryImpl.existsByEmail は mapper 例外を再送出する")
    void should_rethrowException_when_existsByEmailFails() {
        final EmailAddress email = DomainTestFixtures.user().getEmail();
        final RuntimeException expected = new RuntimeException("count by email failed");
        when(userMapper.countByEmail(email)).thenThrow(expected);

        assertThatThrownBy(() -> newRepository().existsByEmail(email))
                .isSameAs(expected);

        verify(userMapper).countByEmail(email);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    @DisplayName("UserRepositoryImpl.saveUser は mapper 例外を再送出する")
    void should_rethrowException_when_saveUserFails() {
        final User user = DomainTestFixtures.user();
        final RuntimeException expected = new RuntimeException("insert failed");
        doThrow(expected).when(userMapper).insert(org.mockito.ArgumentMatchers.any());

        assertThatThrownBy(() -> newRepository().saveUser(user))
                .isSameAs(expected);

        verify(userMapper).insert(org.mockito.ArgumentMatchers.any());
        verifyNoMoreInteractions(userMapper);
    }

    private UserRepositoryImpl newRepository() {
        return new UserRepositoryImpl(userMapper, FIXED_CLOCK);
    }
}
