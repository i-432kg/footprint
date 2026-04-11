package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileExtensionTest {

    @Test
    void of_shouldNormalizeDotAndCase() {
        FileExtension actual = FileExtension.of(".JPG");

        assertThat(actual.getValue()).isEqualTo("jpg");
        assertThat(actual.withDot()).isEqualTo(".jpg");
    }

    @Test
    void of_shouldRejectUnsupportedExtension() {
        assertThatThrownBy(() -> FileExtension.of("bmp"))
                .isInstanceOf(InvalidValueException.class);
    }
}
