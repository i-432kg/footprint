package jp.i432kg.footprint.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.assertInvalidValue;
import static org.assertj.core.api.Assertions.assertThat;

class FileExtensionTest {

    @Test
    @DisplayName("FileExtension.of は先頭ドットを除去し小文字化した拡張子を保持する")
    void should_normalizeFileExtension_when_valueHasDotSpacesAndUpperCase() {
        final FileExtension actual = FileExtension.of(" .JpG ");

        assertThat(actual.getValue()).isEqualTo("jpg");
    }

    @Test
    @DisplayName("FileExtension.of は許可された拡張子を受け入れる")
    void should_createFileExtension_when_valueIsAllowed() {
        assertThat(FileExtension.of("jpeg").getValue()).isEqualTo("jpeg");
        assertThat(FileExtension.of("png").getValue()).isEqualTo("png");
        assertThat(FileExtension.of("gif").getValue()).isEqualTo("gif");
    }

    @Test
    @DisplayName("FileExtension.withDot はドット付きの拡張子表現を返す")
    void should_returnExtensionWithDot_when_withDotIsCalled() {
        final FileExtension actual = FileExtension.of("jpg");

        assertThat(actual.withDot()).isEqualTo(".jpg");
    }

    @Test
    @DisplayName("FileExtension.of は null または空白のみを拒否する")
    void should_throwException_when_fileExtensionIsNullOrBlank() {
        assertInvalidValue(() -> FileExtension.of(null), "extension", "required");
        assertInvalidValue(() -> FileExtension.of(" "), "extension", "blank");
    }

    @Test
    @DisplayName("FileExtension.of は最大長を超える値を拒否する")
    void should_throwException_when_fileExtensionIsTooLong() {
        assertInvalidValue(() -> FileExtension.of("abcdefghijk"), "extension", "too_long");
    }

    @Test
    @DisplayName("FileExtension.of は許可外文字を含む値を拒否する")
    void should_throwException_when_fileExtensionHasInvalidFormat() {
        assertInvalidValue(() -> FileExtension.of("jp-g"), "extension", "invalid_format");
    }

    @Test
    @DisplayName("FileExtension.of は未対応の拡張子を拒否する")
    void should_throwException_when_fileExtensionIsUnsupported() {
        assertInvalidValue(() -> FileExtension.of("bmp"), "extension", "unsupported extension");
        assertInvalidValue(() -> FileExtension.of("webp"), "extension", "unsupported extension");
    }
}
