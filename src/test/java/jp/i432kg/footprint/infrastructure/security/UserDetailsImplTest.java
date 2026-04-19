package jp.i432kg.footprint.infrastructure.security;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.infrastructure.security.mapper.AuthMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserDetailsImplTest {

    @Test
    @DisplayName("UserDetailsImpl.fromEntity は認証 entity から UserDetailsImpl を生成する")
    void should_createUserDetailsFromEntity_when_fromEntityCalled() {
        final AuthMapper.AuthUserEntity entity = authUserEntity();

        final UserDetailsImpl actual = UserDetailsImpl.fromEntity(entity);

        assertThat(actual.getUserId()).isEqualTo(entity.getUserId());
        assertThat(actual.getDisplayUsername()).isEqualTo(entity.getDisplayUsername());
    }

    @Test
    @DisplayName("UserDetailsImpl は認証に必要な username と password を返す")
    void should_returnAuthenticationFields_when_accessorsCalled() {
        final UserDetailsImpl actual = UserDetailsImpl.fromEntity(authUserEntity());

        assertThat(actual.getUsername()).isEqualTo("user@example.com");
        assertThat(actual.getPassword()).isEqualTo("hashed-password");
        assertThat(actual.isAccountNonExpired()).isTrue();
        assertThat(actual.isAccountNonLocked()).isTrue();
        assertThat(actual.isCredentialsNonExpired()).isTrue();
        assertThat(actual.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("UserDetailsImpl.getAuthorities は空の権限リストを返す")
    void should_returnEmptyAuthorities_when_getAuthoritiesCalled() {
        final UserDetailsImpl actual = UserDetailsImpl.fromEntity(authUserEntity());

        assertThat(actual.getAuthorities()).isEmpty();
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
