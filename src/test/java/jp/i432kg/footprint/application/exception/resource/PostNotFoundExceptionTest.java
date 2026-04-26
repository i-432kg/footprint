package jp.i432kg.footprint.application.exception.resource;

import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostNotFoundExceptionTest {

    @Test
    @DisplayName("PostNotFoundException は投稿 not found 情報を組み立てる")
    void should_buildPostNotFoundDetails_when_created() {
        final PostNotFoundException exception =
                new PostNotFoundException(PostId.of("01ARZ3NDEKTSV4RRFFQ69G5FAX"));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo("Post not found. postId=01ARZ3NDEKTSV4RRFFQ69G5FAX");
        assertThat(exception.getDetails())
                .containsEntry("target", "post")
                .containsEntry("reason", "not_found")
                .containsEntry("resourceId", "01ARZ3NDEKTSV4RRFFQ69G5FAX");
    }
}
