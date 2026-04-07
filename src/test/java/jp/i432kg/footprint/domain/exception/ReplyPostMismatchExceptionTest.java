package jp.i432kg.footprint.domain.exception;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReplyPostMismatchExceptionTest {

    @Test
    void constructor_shouldSetMessageErrorCodeAndDetails() {
        ReplyPostMismatchException actual = new ReplyPostMismatchException(
                DomainTestFixtures.otherPostId(),
                DomainTestFixtures.postId()
        );

        assertThat(actual.getMessage()).isEqualTo(
                "Reply post mismatch. expectedPostId=" + DomainTestFixtures.otherPostId()
                        + ", actualPostId=" + DomainTestFixtures.postId()
        );
        assertThat(actual.getErrorCode()).isEqualTo(ErrorCode.REPLY_POST_MISMATCH);
        assertThat(actual.getDetails())
                .containsEntry("target", "reply")
                .containsEntry("reason", "post_mismatch")
                .containsEntry("rejectedValue", DomainTestFixtures.postId())
                .containsEntry("expectedPostId", DomainTestFixtures.otherPostId())
                .containsEntry("actualPostId", DomainTestFixtures.postId());
    }
}
