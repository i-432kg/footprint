package jp.i432kg.footprint.application.query.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReplySummaryTest {

    @Test
    @DisplayName("ReplySummary は親返信ありの返信情報を保持する")
    void should_holdValues_when_parentReplyExists() {
        final LocalDateTime createdAt = LocalDateTime.of(2026, 4, 18, 19, 15, 30);
        final ReplySummary summary = new ReplySummary(
                "reply-01",
                "post-01",
                "user-01",
                "reply-parent-01",
                "message",
                2,
                createdAt
        );

        assertThat(summary.getId()).isEqualTo("reply-01");
        assertThat(summary.getPostId()).isEqualTo("post-01");
        assertThat(summary.getUserId()).isEqualTo("user-01");
        assertThat(summary.getParentReplyId()).isEqualTo("reply-parent-01");
        assertThat(summary.getMessage()).isEqualTo("message");
        assertThat(summary.getChildCount()).isEqualTo(2);
        assertThat(summary.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("ReplySummary はルート返信として null の parentReplyId を保持できる")
    void should_allowNullParentReplyId_when_replyIsTopLevel() {
        final ReplySummary summary = new ReplySummary(
                "reply-01",
                "post-01",
                "user-01",
                null,
                "message",
                0,
                LocalDateTime.of(2026, 4, 18, 19, 15, 30)
        );

        assertThat(summary.getParentReplyId()).isNull();
    }

    @Test
    @DisplayName("ReplySummary は no-args 生成時に既定値で初期化される")
    void should_initializeDefaultValues_when_createdWithNoArgsConstructor() {
        final ReplySummary summary = new ReplySummary();

        assertThat(summary.getId()).isNull();
        assertThat(summary.getPostId()).isNull();
        assertThat(summary.getUserId()).isNull();
        assertThat(summary.getParentReplyId()).isNull();
        assertThat(summary.getMessage()).isNull();
        assertThat(summary.getChildCount()).isNull();
        assertThat(summary.getCreatedAt()).isNull();
    }
}
