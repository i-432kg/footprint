package jp.i432kg.footprint.infrastructure.id;

import jp.i432kg.footprint.domain.value.ImageId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UlidImageIdGeneratorTest {

    private final UlidImageIdGenerator generator = new UlidImageIdGenerator();

    @Test
    @DisplayName("UlidImageIdGenerator.generate は有効な ImageId を生成する")
    void should_generateImageId_when_generateCalled() {
        final ImageId actual = generator.generate();

        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isNotBlank();
        assertThat(ImageId.of(actual.getValue())).isEqualTo(actual);
    }

    @Test
    @DisplayName("UlidImageIdGenerator.generate は連続呼び出しでも有効な ImageId を返す")
    void should_returnValidImageIds_when_generateCalledMultipleTimes() {
        final ImageId first = generator.generate();
        final ImageId second = generator.generate();

        assertThat(first).isNotNull();
        assertThat(second).isNotNull();
        assertThat(first).isNotSameAs(second);
        assertThat(ImageId.of(first.getValue())).isEqualTo(first);
        assertThat(ImageId.of(second.getValue())).isEqualTo(second);
    }
}
