package jp.i432kg.footprint.application.exception.resource;

import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReplyNotFoundExceptionTest {

    @Test
    @DisplayName("ReplyNotFoundException は返信 not found 情報を組み立てる")
    void should_buildReplyNotFoundDetails_when_created() {
        final ReplyNotFoundException exception =
                new ReplyNotFoundException(ReplyId.of("01ARZ3NDEKTSV4RRFFQ69G5FAZ"));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REPLY_NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo("Reply not found. replyId=01ARZ3NDEKTSV4RRFFQ69G5FAZ");
        assertThat(exception.getDetails()).containsEntry("replyId", "01ARZ3NDEKTSV4RRFFQ69G5FAZ");
    }
}
