package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParentReplyTest {

    @Test
    @DisplayName("ParentReply.of は親返信ありの状態を生成する")
    void should_createParentReplyWithReplyId_when_parentReplyExists() {
        final ParentReply actual = ParentReply.of(DomainTestFixtures.replyId());

        assertThat(actual.getReplyId()).isEqualTo(DomainTestFixtures.replyId());
        assertThat(actual.hasParent()).isTrue();
        assertThat(actual.isRoot()).isFalse();
    }

    @Test
    @DisplayName("ParentReply.root は親返信なしの状態を生成する")
    void should_createRootParentReply_when_rootIsCalled() {
        final ParentReply actual = ParentReply.root();

        assertThat(actual.getReplyId()).isNull();
        assertThat(actual.hasParent()).isFalse();
        assertThat(actual.isRoot()).isTrue();
    }

    @Test
    @DisplayName("ParentReply.getReplyId はルート返信の場合に null を返す")
    void should_returnNullReplyId_when_parentReplyIsRoot() {
        final ParentReply actual = ParentReply.root();

        assertThat(actual.getReplyId()).isNull();
    }
}
