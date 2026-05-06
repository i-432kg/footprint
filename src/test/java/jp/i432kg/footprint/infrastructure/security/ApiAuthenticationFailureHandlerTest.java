package jp.i432kg.footprint.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

class ApiAuthenticationFailureHandlerTest {

    private final ApiAuthenticationFailureHandler handler = new ApiAuthenticationFailureHandler();

    @Test
    @DisplayName("ApiAuthenticationFailureHandler は認証失敗時に 401 を返す")
    void should_returnUnauthorized_when_authenticationFails() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/login");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationFailure(request, response, new BadCredentialsException("bad credentials"));

        assertThat(response.getStatus()).isEqualTo(MockHttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getErrorMessage()).isEqualTo("Authentication Failed");
    }
}
