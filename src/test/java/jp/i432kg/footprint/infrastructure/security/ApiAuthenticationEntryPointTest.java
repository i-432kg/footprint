package jp.i432kg.footprint.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.InsufficientAuthenticationException;

import static org.assertj.core.api.Assertions.assertThat;

class ApiAuthenticationEntryPointTest {

    private final ApiAuthenticationEntryPoint entryPoint = new ApiAuthenticationEntryPoint();

    @Test
    @DisplayName("ApiAuthenticationEntryPoint は未認証アクセス時に 401 を返す")
    void should_returnUnauthorized_when_authenticationIsRequired() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/posts");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(request, response, new InsufficientAuthenticationException("auth required"));

        assertThat(response.getStatus()).isEqualTo(MockHttpServletResponse.SC_UNAUTHORIZED);
    }
}
