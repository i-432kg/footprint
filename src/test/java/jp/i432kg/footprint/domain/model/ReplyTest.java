package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReplyTest {

    @Test
    @DisplayName("Reply.of は妥当な値から返信を生成できる")
    void should_createReply_when_valuesAreValid() {
        final Reply actual = Reply.of(
                DomainTestFixtures.replyId(),
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                ParentReply.of(DomainTestFixtures.otherReplyId()),
                DomainTestFixtures.replyMessage(),
                LocalDateTime.of(2026, 4, 1, 15, 0)
        );

        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("Reply は親返信ありの場合に親返信 ID と true を返す")
    void should_returnParentReplyState_when_replyHasParent() {
        final Reply actual = Reply.of(
                DomainTestFixtures.replyId(),
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                ParentReply.of(DomainTestFixtures.otherReplyId()),
                DomainTestFixtures.replyMessage(),
                LocalDateTime.of(2026, 4, 1, 15, 0)
        );

        assertThat(actual.getReplyId()).isEqualTo(DomainTestFixtures.replyId());
        assertThat(actual.getPostId()).isEqualTo(DomainTestFixtures.postId());
        assertThat(actual.getUserId()).isEqualTo(DomainTestFixtures.userId());
        assertThat(actual.getMessage()).isEqualTo(DomainTestFixtures.replyMessage());
        assertThat(actual.getParentReplyId()).isEqualTo(DomainTestFixtures.otherReplyId());
        assertThat(actual.hasParentReply()).isTrue();
    }

    @Test
    @DisplayName("Reply はルート返信の場合に親返信 ID と false を返す")
    void should_returnRootReplyState_when_replyHasNoParent() {
        final Reply actual = DomainTestFixtures.reply();

        assertThat(actual.getParentReplyId()).isNull();
        assertThat(actual.hasParentReply()).isFalse();
    }
}
