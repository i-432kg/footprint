package jp.i432kg.footprint.application.exception.usecase;

import jp.i432kg.footprint.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReplyCommandFailedExceptionTest {

    @Test
    @DisplayName("ReplyCommandFailedException.saveFailed は保存失敗情報を組み立てる")
    void should_buildSaveFailedDetails_when_factoryIsUsed() {
        final IllegalStateException cause = new IllegalStateException("db down");
        final ReplyCommandFailedException exception = ReplyCommandFailedException.saveFailed(cause);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REPLY_COMMAND_FAILED);
        assertThat(exception.getMessage()).isEqualTo("reply is invalid. reason=save_failed");
        assertThat(exception.getDetails())
                .containsEntry("target", "reply")
                .containsEntry("reason", "save_failed")
                .doesNotContainKey("rejectedValue");
        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    @DisplayName("ReplyCommandFailedException.increaseReplyCountFailed は返信数更新失敗情報を組み立てる")
    void should_buildIncreaseReplyCountFailedDetails_when_factoryIsUsed() {
        final IllegalStateException cause = new IllegalStateException("db down");
        final ReplyCommandFailedException exception =
                ReplyCommandFailedException.increaseReplyCountFailed(cause);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REPLY_COMMAND_FAILED);
        assertThat(exception.getMessage()).isEqualTo("reply is invalid. reason=increase_reply_count_failed");
        assertThat(exception.getDetails())
                .containsEntry("target", "reply")
                .containsEntry("reason", "increase_reply_count_failed")
                .doesNotContainKey("rejectedValue");
        assertThat(exception.getCause()).isSameAs(cause);
    }
}
