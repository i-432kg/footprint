package jp.i432kg.footprint.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;

import static org.assertj.core.api.Assertions.assertThat;

class ApiAccessDeniedHandlerTest {

    private final ApiAccessDeniedHandler handler = new ApiAccessDeniedHandler();

    @Test
    @DisplayName("ApiAccessDeniedHandler は認可失敗時に 403 を返す")
    void should_returnForbidden_when_accessIsDenied() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/posts");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(request, response, new AccessDeniedException("forbidden"));

        assertThat(response.getStatus()).isEqualTo(MockHttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("ApiAccessDeniedHandler は CSRF 欠落時に 403 を返す")
    void should_returnForbidden_when_csrfTokenIsMissing() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/posts");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(request, response, new MissingCsrfTokenException(null));

        assertThat(response.getStatus()).isEqualTo(MockHttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("ApiAccessDeniedHandler は CSRF 不正時に 403 を返す")
    void should_returnForbidden_when_csrfTokenIsInvalid() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/posts");
        final MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(
                request,
                response,
                new InvalidCsrfTokenException(new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "expected"), "actual")
        );

        assertThat(response.getStatus()).isEqualTo(MockHttpServletResponse.SC_FORBIDDEN);
    }
}
