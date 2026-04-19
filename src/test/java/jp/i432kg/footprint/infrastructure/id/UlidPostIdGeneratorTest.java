package jp.i432kg.footprint.infrastructure.id;

import jp.i432kg.footprint.domain.value.PostId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UlidPostIdGeneratorTest {

    private final UlidPostIdGenerator generator = new UlidPostIdGenerator();

    @Test
    @DisplayName("UlidPostIdGenerator.generate は有効な PostId を生成する")
    void should_generatePostId_when_generateCalled() {
        final PostId actual = generator.generate();

        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isNotBlank();
        assertThat(PostId.of(actual.getValue())).isEqualTo(actual);
    }

    @Test
    @DisplayName("UlidPostIdGenerator.generate は連続呼び出しでも有効な PostId を返す")
    void should_returnValidPostIds_when_generateCalledMultipleTimes() {
        final PostId first = generator.generate();
        final PostId second = generator.generate();

        assertThat(first).isNotNull();
        assertThat(second).isNotNull();
        assertThat(first).isNotSameAs(second);
        assertThat(PostId.of(first.getValue())).isEqualTo(first);
        assertThat(PostId.of(second.getValue())).isEqualTo(second);
    }
}
