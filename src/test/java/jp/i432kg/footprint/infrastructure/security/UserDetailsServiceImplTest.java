package jp.i432kg.footprint.infrastructure.security;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.infrastructure.security.mapper.AuthMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private AuthMapper authMapper;

    @Test
    @DisplayName("UserDetailsServiceImpl.loadUserByUsername は email から認証ユーザーを取得して UserDetailsImpl を返す")
    void should_returnUserDetails_when_userExists() {
        final AuthMapper.AuthUserEntity entity = authUserEntity();
        when(authMapper.findAuthUserByLoginId(EmailAddress.of("user@example.com")))
                .thenReturn(Optional.of(entity));

        final UserDetailsImpl actual = newService().loadUserByUsername("user@example.com");

        assertThat(actual.getUserId()).isEqualTo(entity.getUserId());
        assertThat(actual.getUsername()).isEqualTo(entity.getEmail());
        assertThat(actual.getDisplayUsername()).isEqualTo(entity.getDisplayUsername());
        verify(authMapper).findAuthUserByLoginId(EmailAddress.of("user@example.com"));
    }

    @Test
    @DisplayName("UserDetailsServiceImpl.loadUserByUsername はユーザーが存在しない場合に UsernameNotFoundException を送出する")
    void should_throwUsernameNotFoundException_when_userDoesNotExist() {
        when(authMapper.findAuthUserByLoginId(EmailAddress.of("user@example.com")))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> newService().loadUserByUsername("user@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found: user@example.com");

        verify(authMapper).findAuthUserByLoginId(EmailAddress.of("user@example.com"));
    }

    private UserDetailsServiceImpl newService() {
        return new UserDetailsServiceImpl(authMapper);
    }

    private static AuthMapper.AuthUserEntity authUserEntity() {
        return new AuthMapper.AuthUserEntity(
                DomainTestFixtures.userId(),
                "user@example.com",
                "user",
                "hashed-password"
        );
    }
}
