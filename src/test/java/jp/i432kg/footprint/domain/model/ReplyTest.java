package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReplyTest {

    @Test
    void of_shouldCreateInstanceWithGivenValues() {
        final LocalDateTime createdAt = LocalDateTime.of(2026, 4, 1, 15, 0);

        final Reply actual = Reply.of(
                DomainTestFixtures.replyId(),
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                ParentReply.of(DomainTestFixtures.otherReplyId()),
                DomainTestFixtures.replyMessage(),
                createdAt
        );

        assertThat(actual.getReplyId()).isEqualTo(DomainTestFixtures.replyId());
        assertThat(actual.getPostId()).isEqualTo(DomainTestFixtures.postId());
        assertThat(actual.getUserId()).isEqualTo(DomainTestFixtures.userId());
        assertThat(actual.getParentReplyId()).isEqualTo(DomainTestFixtures.otherReplyId());
        assertThat(actual.getMessage()).isEqualTo(DomainTestFixtures.replyMessage());
        assertThat(actual.getCreatedAt()).isEqualTo(createdAt);
        assertThat(actual.getParentReply().hasParent()).isTrue();
    }

    @Test
    void hasParentReply_shouldReturnTrue_whenParentReplyIdExists() {
        final Reply actual = Reply.of(
                DomainTestFixtures.replyId(),
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                ParentReply.of(DomainTestFixtures.otherReplyId()),
                DomainTestFixtures.replyMessage(),
                LocalDateTime.of(2026, 4, 1, 15, 0)
        );

        assertThat(actual.hasParentReply()).isTrue();
    }

    @Test
    void hasParentReply_shouldReturnFalse_whenParentReplyIdIsNull() {
        assertThat(DomainTestFixtures.reply().hasParentReply()).isFalse();
    }
}
