package jp.i432kg.footprint.domain.exception;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReplyPostMismatchExceptionTest {

    @Test
    @DisplayName("ReplyPostMismatchException は message と errorCode と details を保持する")
    void should_setMessageErrorCodeAndDetails_when_constructed() {
        final ReplyPostMismatchException actual = new ReplyPostMismatchException(
                DomainTestFixtures.otherPostId(),
                DomainTestFixtures.postId()
        );

        assertThat(actual.getMessage()).isEqualTo(
                "Reply post mismatch. expectedPostId=" + DomainTestFixtures.otherPostId()
                        + ", actualPostId=" + DomainTestFixtures.postId()
        );
        assertThat(actual.getErrorCode()).isEqualTo(ErrorCode.REPLY_POST_MISMATCH);
        assertThat(actual.getDetails())
                .containsEntry("target", "reply.postId")
                .containsEntry("reason", "post_mismatch")
                .doesNotContainKey("rejectedValue")
                .containsEntry("expectedPostId", DomainTestFixtures.otherPostId().getValue())
                .containsEntry("actualPostId", DomainTestFixtures.postId().getValue());
    }
}
