package jp.i432kg.footprint.infrastructure.id;

import jp.i432kg.footprint.domain.value.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UlidUserIdGeneratorTest {

    private final UlidUserIdGenerator generator = new UlidUserIdGenerator();

    @Test
    @DisplayName("UlidUserIdGenerator.generate は有効な UserId を生成する")
    void should_generateUserId_when_generateCalled() {
        final UserId actual = generator.generate();

        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isNotBlank();
        assertThat(UserId.of(actual.getValue())).isEqualTo(actual);
    }

    @Test
    @DisplayName("UlidUserIdGenerator.generate は連続呼び出しでも有効な UserId を返す")
    void should_returnValidUserIds_when_generateCalledMultipleTimes() {
        final UserId first = generator.generate();
        final UserId second = generator.generate();

        assertThat(first).isNotNull();
        assertThat(second).isNotNull();
        assertThat(first).isNotSameAs(second);
        assertThat(UserId.of(first.getValue())).isEqualTo(first);
        assertThat(UserId.of(second.getValue())).isEqualTo(second);
    }
}
