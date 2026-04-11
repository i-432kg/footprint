package jp.i432kg.footprint.infrastructure.security;

import jakarta.servlet.http.HttpServletResponse;
import jp.i432kg.footprint.infrastructure.security.mapper.AuthMapper;
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

@ExtendWith(MockitoExtension.class)
class LastLoginUpdatingAuthenticationSuccessHandlerTest {

    @Mock
    private LastLoginRecorder lastLoginRecorder;

    @Test
    void onAuthenticationSuccess_shouldDelegateToRecorderAndReturnOk() throws Exception {
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
}
