package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StorageTypeTest {

    @Test
    void of_shouldResolveCaseInsensitiveValue() {
        StorageType actual = StorageType.of("local");

        assertThat(actual).isEqualTo(StorageType.LOCAL);
    }

    @Test
    void of_shouldRejectUnknownValue() {
        assertThatThrownBy(() -> StorageType.of("gcs"))
                .isInstanceOf(InvalidValueException.class);
    }
}
