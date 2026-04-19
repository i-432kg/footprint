package jp.i432kg.footprint.infrastructure.security;

import jakarta.servlet.http.HttpServletResponse;
import jp.i432kg.footprint.infrastructure.security.mapper.AuthMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class LastLoginUpdatingAuthenticationSuccessHandlerTest {

    @Mock
    private LastLoginRecorder lastLoginRecorder;

    @Test
    @DisplayName("LastLoginUpdatingAuthenticationSuccessHandler.onAuthenticationSuccess は UserDetailsImpl の principal を recorder へ委譲して 200 を返す")
    void should_delegateToRecorderAndReturnOk_when_principalIsUserDetailsImpl() {
        final var handler = new LastLoginUpdatingAuthenticationSuccessHandler(lastLoginRecorder);
        final var principal = UserDetailsImpl.fromEntity(
                new AuthMapper.AuthUserEntity(
                        jp.i432kg.footprint.domain.value.UserId.of("01JQW8D4Q3G9Y2X6N7M8P9R0ST"),
                        "user@example.com",
                        "user",
                        "hashed-password"
                )
        );
        final Authentication authentication =
                UsernamePasswordAuthenticationToken.authenticated(principal, null, principal.getAuthorities());
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(lastLoginRecorder).recordSuccessfulLogin(principal.getUserId());
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("LastLoginUpdatingAuthenticationSuccessHandler.onAuthenticationSuccess は principal が UserDetailsImpl でない場合も 200 を返す")
    void should_returnOkWithoutDelegation_when_principalIsNotUserDetailsImpl() {
        final var handler = new LastLoginUpdatingAuthenticationSuccessHandler(lastLoginRecorder);
        final Authentication authentication =
                UsernamePasswordAuthenticationToken.authenticated("anonymousUser", null, java.util.List.of());
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, authentication);

        verifyNoInteractions(lastLoginRecorder);
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
    }
}
