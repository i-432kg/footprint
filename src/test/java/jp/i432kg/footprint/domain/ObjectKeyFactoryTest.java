package jp.i432kg.footprint.domain;

import jp.i432kg.footprint.domain.value.FileExtension;
import jp.i432kg.footprint.domain.value.ImageId;
import jp.i432kg.footprint.domain.value.ObjectKey;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.UserId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObjectKeyFactoryTest {

    @Test
    void createPostImageKey_shouldCreateExpectedKey_whenAllArgumentsAreValid() {
        UserId userId = DomainTestFixtures.userId();
        PostId postId = DomainTestFixtures.postId();
        ImageId imageId = DomainTestFixtures.imageId();
        FileExtension extension = FileExtension.of("jpg");

        ObjectKey actual = ObjectKeyFactory.createPostImageKey(userId, postId, imageId, extension);

        assertThat(actual.getValue())
                .isEqualTo("users/01ARZ3NDEKTSV4RRFFQ69G5FAV/posts/01ARZ3NDEKTSV4RRFFQ69G5FAX/images/01ARZ3NDEKTSV4RRFFQ69G5FB1.jpg");
    }

    @Test
    void createPostImageKey_shouldThrowIllegalArgumentException_whenUserIdIsNull() {
        assertThatThrownBy(() -> ObjectKeyFactory.createPostImageKey(
                null,
                DomainTestFixtures.postId(),
                DomainTestFixtures.imageId(),
                FileExtension.of("jpg")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("userId must not be null");
    }

    @Test
    void createPostImageKey_shouldThrowIllegalArgumentException_whenPostIdIsNull() {
        assertThatThrownBy(() -> ObjectKeyFactory.createPostImageKey(
                DomainTestFixtures.userId(),
                null,
                DomainTestFixtures.imageId(),
                FileExtension.of("jpg")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("postId must not be null");
    }

    @Test
    void createPostImageKey_shouldThrowIllegalArgumentException_whenImageIdIsNull() {
        assertThatThrownBy(() -> ObjectKeyFactory.createPostImageKey(
                DomainTestFixtures.userId(),
                DomainTestFixtures.postId(),
                null,
                FileExtension.of("jpg")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("imageId must not be null");
    }

    @Test
    void createPostImageKey_shouldThrowIllegalArgumentException_whenExtensionIsNull() {
        assertThatThrownBy(() -> ObjectKeyFactory.createPostImageKey(
                DomainTestFixtures.userId(),
                DomainTestFixtures.postId(),
                DomainTestFixtures.imageId(),
                null
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("extension must not be null");
    }
}
