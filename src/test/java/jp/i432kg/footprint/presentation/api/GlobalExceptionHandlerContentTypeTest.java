package jp.i432kg.footprint.presentation.api;

import jp.i432kg.footprint.application.exception.resource.PostNotFoundException;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.logging.masking.SensitiveDataMasker;
import jp.i432kg.footprint.logging.operation.FailureEventResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerContentTypeTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler(new FailureEventResolver(), new SensitiveDataMasker()))
                .build();
    }

    @Test
    @DisplayName("GlobalExceptionHandler は resource 例外を application/problem+json で返す")
    void should_returnProblemJson_when_resourceExceptionIsHandled() throws Exception {
        mockMvc.perform(get("/test/posts/01ARZ3NDEKTSV4RRFFQ69G5FAX"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    @DisplayName("GlobalExceptionHandler は validation 例外を application/problem+json で返す")
    void should_returnProblemJson_when_validationExceptionIsHandled() throws Exception {
        mockMvc.perform(get("/test/type-mismatch").param("size", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @RestController
    static class TestController {

        @GetMapping("/test/posts/{postId}")
        String throwPostNotFound() {
            throw new PostNotFoundException(PostId.of("01ARZ3NDEKTSV4RRFFQ69G5FAX"));
        }

        @GetMapping("/test/type-mismatch")
        String typeMismatch(@RequestParam final Integer size) {
            return String.valueOf(size);
        }
    }
}
