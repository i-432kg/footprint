package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.assertInvalidValue;
import static org.assertj.core.api.Assertions.assertThat;

class ObjectKeyTest {

    @Test
    @DisplayName("ObjectKey.of は前後空白を除去して保持する")
    void should_trimObjectKey_when_valueHasLeadingOrTrailingSpaces() {
        final ObjectKey actual = ObjectKey.of(" users/123/posts/456/image.jpg ");

        assertThat(actual.getValue()).isEqualTo("users/123/posts/456/image.jpg");
    }

    @Test
    @DisplayName("ObjectKey.of は安全なパス形式の値を受け入れる")
    void should_createObjectKey_when_valueIsSafePath() {
        final ObjectKey actual = ObjectKey.of("users/123/images/abc_01.jpg");

        assertThat(actual.getValue()).isEqualTo("users/123/images/abc_01.jpg");
    }

    @Test
    @DisplayName("ObjectKey.of は null または空白のみを拒否する")
    void should_throwException_when_objectKeyIsNullOrBlank() {
        assertInvalidValue(() -> ObjectKey.of(null), "object_key", "required");
        assertInvalidValue(() -> ObjectKey.of(" "), "object_key", "blank");
    }

    @Test
    @DisplayName("ObjectKey.of は最大長を超える値を拒否する")
    void should_throwException_when_objectKeyExceedsMaxLength() {
        assertInvalidValue(() -> ObjectKey.of("a".repeat(1025)), "object_key", "too_long");
    }

    @Test
    @DisplayName("ObjectKey.of は最大長ちょうどの値を受け入れる")
    void should_createObjectKey_when_valueLengthIsMaxBoundary() {
        final String value = "a".repeat(1024);

        final ObjectKey actual = ObjectKey.of(value);

        assertThat(actual.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("ObjectKey.of は絶対パス表現を拒否する")
    void should_throwException_when_objectKeyStartsWithSlash() {
        assertInvalidValue(() -> ObjectKey.of("/abc"), "object_key", "cannot start with \"/\"");
    }

    @Test
    @DisplayName("ObjectKey.of は Windows パスを含む値を拒否する")
    void should_throwException_when_objectKeyContainsBackslash() {
        assertInvalidValue(() -> ObjectKey.of("abc\\def"), "object_key", "cannot contain \"\\\\\"");
    }

    @Test
    @DisplayName("ObjectKey.of は . や .. を含むセグメントを拒否する")
    void should_throwException_when_objectKeyContainsInvalidSegments() {
        assertInvalidValue(() -> ObjectKey.of("abc//def"), "object_key", "cannot contain \"//\"");
        assertInvalidValue(() -> ObjectKey.of("./a"), "object_key", "cannot contain \".\" or \"..\" or empty segment");
        assertInvalidValue(() -> ObjectKey.of("../a"), "object_key", "cannot contain \".\" or \"..\" or empty segment");
        assertInvalidValue(() -> ObjectKey.of("a/./b"), "object_key", "cannot contain \".\" or \"..\" or empty segment");
        assertInvalidValue(() -> ObjectKey.of("a/../b"), "object_key", "cannot contain \".\" or \"..\" or empty segment");
        assertInvalidValue(() -> ObjectKey.of("a/"), "object_key", "cannot end with \"/\"");
    }

    @Test
    @DisplayName("ObjectKey.of は許可外文字を含む値を拒否する")
    void should_throwException_when_objectKeyContainsUnsupportedCharacters() {
        assertInvalidValue(() -> ObjectKey.of("a b/c.jpg"), "object_key", "invalid_format");
    }

    @Test
    @DisplayName("ObjectKey.createPostImageKey は投稿画像用のオブジェクトキーを生成する")
    void should_createPostImageKey_when_argumentsAreValid() {
        final ObjectKey actual = ObjectKey.createPostImageKey(
                DomainTestFixtures.userId(),
                DomainTestFixtures.postId(),
                DomainTestFixtures.imageId(),
                FileExtension.of("jpg")
        );

        assertThat(actual.getValue())
                .isEqualTo("users/01ARZ3NDEKTSV4RRFFQ69G5FAV/posts/01ARZ3NDEKTSV4RRFFQ69G5FAX/images/01ARZ3NDEKTSV4RRFFQ69G5FB1.jpg");
    }

    @Test
    @DisplayName("ObjectKey.createPostImageKey は null の引数を拒否する")
    void should_throwException_when_createPostImageKeyArgumentsAreNull() {
        assertInvalidValue(
                () -> ObjectKey.createPostImageKey(
                        null,
                        DomainTestFixtures.postId(),
                        DomainTestFixtures.imageId(),
                        FileExtension.of("jpg")
                ),
                "user_id",
                "required"
        );
        assertInvalidValue(
                () -> ObjectKey.createPostImageKey(
                        DomainTestFixtures.userId(),
                        null,
                        DomainTestFixtures.imageId(),
                        FileExtension.of("jpg")
                ),
                "post_id",
                "required"
        );
        assertInvalidValue(
                () -> ObjectKey.createPostImageKey(
                        DomainTestFixtures.userId(),
                        DomainTestFixtures.postId(),
                        null,
                        FileExtension.of("jpg")
                ),
                "image_id",
                "required"
        );
        assertInvalidValue(
                () -> ObjectKey.createPostImageKey(
                        DomainTestFixtures.userId(),
                        DomainTestFixtures.postId(),
                        DomainTestFixtures.imageId(),
                        null
                ),
                "file_extension",
                "required"
        );
    }
}
