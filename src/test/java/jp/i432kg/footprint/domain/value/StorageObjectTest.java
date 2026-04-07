package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StorageObjectTest {

    @Test
    void of_shouldCreateInstanceWithGivenValues() {
        StorageObject actual = StorageObject.of(
                StorageType.LOCAL,
                ObjectKey.of("users/123/posts/456/images/abc123.jpg")
        );

        assertThat(actual.getStorageType()).isEqualTo(StorageType.LOCAL);
        assertThat(actual.getObjectKey().getValue()).isEqualTo("users/123/posts/456/images/abc123.jpg");
    }

    @Test
    void local_shouldCreateLocalStorageObject() {
        StorageObject actual = StorageObject.local(ObjectKey.of("users/123/posts/456/images/abc123.jpg"));

        assertThat(actual.isLocal()).isTrue();
        assertThat(actual.isS3()).isFalse();
    }

    @Test
    void s3_shouldCreateS3StorageObject() {
        StorageObject actual = StorageObject.s3(ObjectKey.of("users/123/posts/456/images/abc123.jpg"));

        assertThat(actual.isS3()).isTrue();
        assertThat(actual.isLocal()).isFalse();
    }

    @Test
    void of_shouldRejectNullStorageType() {
        assertThatThrownBy(() -> StorageObject.of(null, ObjectKey.of("users/123/posts/456/images/abc123.jpg")))
                .isInstanceOf(InvalidValueException.class);
    }
}
