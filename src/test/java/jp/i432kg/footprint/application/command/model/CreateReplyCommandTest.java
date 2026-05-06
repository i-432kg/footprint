package jp.i432kg.footprint.application.command.model;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.model.ParentReply;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateReplyCommandTest {

    @Test
    @DisplayName("CreateReplyCommand.of は渡された値を保持する")
    void should_createCommand_when_valuesAreProvided() {
        final ParentReply parentReply = ParentReply.of(DomainTestFixtures.replyId());

        final CreateReplyCommand actual = CreateReplyCommand.of(
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                parentReply,
                DomainTestFixtures.replyMessage()
        );

        assertThat(actual.getPostId()).isEqualTo(DomainTestFixtures.postId());
        assertThat(actual.getUserId()).isEqualTo(DomainTestFixtures.userId());
        assertThat(actual.getParentReply()).isEqualTo(parentReply);
        assertThat(actual.getMessage()).isEqualTo(DomainTestFixtures.replyMessage());
    }
}
