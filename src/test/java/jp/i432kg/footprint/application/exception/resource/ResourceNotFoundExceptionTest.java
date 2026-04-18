package jp.i432kg.footprint.application.exception.resource;

import jp.i432kg.footprint.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceNotFoundExceptionTest {

    @Test
    @DisplayName("ResourceNotFoundException は not found 情報を保持する")
    void should_holdBasicProperties_when_created() {
        final TestResourceNotFoundException exception = new TestResourceNotFoundException(
                ErrorCode.POST_NOT_FOUND,
                "resource not found",
                Map.of("resourceId", "01ARZ3NDEKTSV4RRFFQ69G5FAV")
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo("resource not found");
        assertThat(exception.getDetails()).containsEntry("resourceId", "01ARZ3NDEKTSV4RRFFQ69G5FAV");
    }

    private static final class TestResourceNotFoundException extends ResourceNotFoundException {
        private TestResourceNotFoundException(
                final ErrorCode errorCode,
                final String message,
                final Map<String, Object> details
        ) {
            super(errorCode, message, details);
        }
    }
}
