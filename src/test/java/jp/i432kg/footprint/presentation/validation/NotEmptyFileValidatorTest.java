package jp.i432kg.footprint.presentation.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;

class NotEmptyFileValidatorTest {

    private final NotEmptyFileValidator validator = new NotEmptyFileValidator();

    @Test
    @DisplayName("NotEmptyFileValidator は null ファイルを拒否する")
    void should_returnFalse_when_fileIsNull() {
        final boolean actual = validator.isValid(null, null);

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("NotEmptyFileValidator は空ファイルを拒否する")
    void should_returnFalse_when_fileIsEmpty() {
        final MockMultipartFile file =
                new MockMultipartFile("imageFile", "empty.jpg", "image/jpeg", new byte[0]);

        final boolean actual = validator.isValid(file, null);

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("NotEmptyFileValidator は非空ファイルを受け入れる")
    void should_returnTrue_when_fileIsNotEmpty() {
        final MockMultipartFile file =
                new MockMultipartFile("imageFile", "post.jpg", "image/jpeg", new byte[] {1, 2, 3});

        final boolean actual = validator.isValid(file, null);

        assertThat(actual).isTrue();
    }

}
