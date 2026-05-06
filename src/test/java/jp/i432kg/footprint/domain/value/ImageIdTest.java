package jp.i432kg.footprint.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.VALID_ULID;
import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.assertInvalidValue;
import static org.assertj.core.api.Assertions.assertThat;

class ImageIdTest {

    @Test
    @DisplayName("ImageId.of は妥当な ULID を受け入れる")
    void should_createImageId_when_valueIsValidUlid() {
        final ImageId actual = ImageId.of(VALID_ULID);

        assertThat(actual.getValue()).isEqualTo(VALID_ULID);
    }

    @Test
    @DisplayName("ImageId.of は null または空白のみを拒否する")
    void should_throwException_when_imageIdIsNullOrBlank() {
        assertInvalidValue(() -> ImageId.of(null), "image_id", "required");
        assertInvalidValue(() -> ImageId.of(" "), "image_id", "blank");
    }

    @Test
    @DisplayName("ImageId.of は ULID 形式でない値を拒否する")
    void should_throwException_when_imageIdFormatIsInvalid() {
        assertInvalidValue(() -> ImageId.of("01ARZ3NDEKTSV4RRFFQ69G5FA"), "image_id", "invalid_format");
        assertInvalidValue(() -> ImageId.of("01ARZ3NDEKTSV4RRFFQ69G5FAVX"), "image_id", "invalid_format");
        assertInvalidValue(() -> ImageId.of("01arz3ndektsv4rrffq69g5fav"), "image_id", "invalid_format");
        assertInvalidValue(() -> ImageId.of("01ARZ3NDEKTSV4RRFFQ69G5FIO"), "image_id", "invalid_format");
    }
}
