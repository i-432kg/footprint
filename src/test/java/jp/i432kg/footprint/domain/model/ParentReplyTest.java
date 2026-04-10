package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParentReplyTest {

    @Test
    void of_shouldCreateStateWithParentReplyId() {
        final ParentReply actual = ParentReply.of(DomainTestFixtures.replyId());

        assertThat(actual.getReplyId()).isEqualTo(DomainTestFixtures.replyId());
        assertThat(actual.hasParent()).isTrue();
        assertThat(actual.isRoot()).isFalse();
    }

    @Test
    void root_shouldCreateStateWithoutParentReplyId() {
        final ParentReply actual = ParentReply.root();

        assertThat(actual.getReplyId()).isNull();
        assertThat(actual.hasParent()).isFalse();
        assertThat(actual.isRoot()).isTrue();
    }
}
