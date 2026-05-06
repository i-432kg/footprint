package jp.i432kg.footprint.infrastructure.id;

import jp.i432kg.footprint.domain.value.ReplyId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UlidReplyIdGeneratorTest {

    private final UlidReplyIdGenerator generator = new UlidReplyIdGenerator();

    @Test
    @DisplayName("UlidReplyIdGenerator.generate は有効な ReplyId を生成する")
    void should_generateReplyId_when_generateCalled() {
        final ReplyId actual = generator.generate();

        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isNotBlank();
        assertThat(ReplyId.of(actual.getValue())).isEqualTo(actual);
    }

    @Test
    @DisplayName("UlidReplyIdGenerator.generate は連続呼び出しでも有効な ReplyId を返す")
    void should_returnValidReplyIds_when_generateCalledMultipleTimes() {
        final ReplyId first = generator.generate();
        final ReplyId second = generator.generate();

        assertThat(first).isNotNull();
        assertThat(second).isNotNull();
        assertThat(first).isNotSameAs(second);
        assertThat(ReplyId.of(first.getValue())).isEqualTo(first);
        assertThat(ReplyId.of(second.getValue())).isEqualTo(second);
    }
}
