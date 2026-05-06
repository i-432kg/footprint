package jp.i432kg.footprint.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.assertInvalidValue;
import static org.assertj.core.api.Assertions.assertThat;

class FileNameTest {

    @Test
    @DisplayName("FileName.of は妥当なファイル名を受け入れる")
    void should_createFileName_when_valueIsValid() {
        final FileName actual = FileName.of("photo_01.jpg");

        assertThat(actual.getValue()).isEqualTo("photo_01.jpg");
    }

    @Test
    @DisplayName("FileName.of は null または空白のみを拒否する")
    void should_throwException_when_fileNameIsNullOrBlank() {
        assertInvalidValue(() -> FileName.of(null), "filename", "required");
        assertInvalidValue(() -> FileName.of(" "), "filename", "blank");
    }

    @Test
    @DisplayName("FileName.of は最大長ちょうどの値を受け入れる")
    void should_createFileName_when_valueLengthIsMaxBoundary() {
        final String value = "a".repeat(254);

        final FileName actual = FileName.of(value);

        assertThat(actual.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("FileName.of は最大長を超える値を拒否する")
    void should_throwException_when_fileNameExceedsMaxLength() {
        assertInvalidValue(() -> FileName.of("a".repeat(255)), "filename", "too_long");
    }

    @Test
    @DisplayName("FileName.of は危険文字を含む値を拒否する")
    void should_throwException_when_fileNameContainsInvalidCharacters() {
        assertInvalidValue(() -> FileName.of("a/b.jpg"), "filename", "invalid_format");
        assertInvalidValue(() -> FileName.of("a\\b.jpg"), "filename", "invalid_format");
        assertInvalidValue(() -> FileName.of("a:b.jpg"), "filename", "invalid_format");
        assertInvalidValue(() -> FileName.of("a*b.jpg"), "filename", "invalid_format");
        assertInvalidValue(() -> FileName.of("a?b.jpg"), "filename", "invalid_format");
        assertInvalidValue(() -> FileName.of("a\"b.jpg"), "filename", "invalid_format");
        assertInvalidValue(() -> FileName.of("a<b.jpg"), "filename", "invalid_format");
        assertInvalidValue(() -> FileName.of("a>b.jpg"), "filename", "invalid_format");
        assertInvalidValue(() -> FileName.of("a|b.jpg"), "filename", "invalid_format");
    }

    @Test
    @DisplayName("FileName.of は .. を含む値を拒否する")
    void should_throwException_when_fileNameContainsTraversalPattern() {
        assertInvalidValue(() -> FileName.of("abc..jpg"), "filename", "cannot contain \"..\"");
    }
}
