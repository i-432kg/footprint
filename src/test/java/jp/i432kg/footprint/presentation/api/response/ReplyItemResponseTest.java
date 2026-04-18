package jp.i432kg.footprint.presentation.api.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class ReplyItemResponseTest {

    @Test
    @DisplayName("ReplyItemResponse は設定された返信情報を保持する")
    void should_holdValues_when_allFieldsAreSet() {
        final OffsetDateTime createdAt = OffsetDateTime.of(2026, 4, 19, 10, 15, 30, 0, ZoneOffset.UTC);
        final ReplyItemResponse response = ReplyItemResponse.of(
                "reply-01",
                "post-01",
                "reply-parent-01",
                "reply message",
                2,
                createdAt
        );

        assertThat(response.getId()).isEqualTo("reply-01");
        assertThat(response.getPostId()).isEqualTo("post-01");
        assertThat(response.getParentReplyId()).isEqualTo("reply-parent-01");
        assertThat(response.getMessage()).isEqualTo("reply message");
        assertThat(response.getChildCount()).isEqualTo(2);
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("ReplyItemResponse はルート返信として null の parentReplyId を保持できる")
    void should_allowNullParentReplyId_when_replyIsRoot() {
        final OffsetDateTime createdAt = OffsetDateTime.of(2026, 4, 19, 10, 15, 30, 0, ZoneOffset.UTC);
        final ReplyItemResponse response = ReplyItemResponse.of(
                "reply-01",
                "post-01",
                null,
                "reply message",
                0,
                createdAt
        );

        assertThat(response.getParentReplyId()).isNull();
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
    }

}
