package jp.i432kg.footprint.presentation.api.request;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Set;

class PostRequestTest extends RequestValidationTestSupport {

    @Test
    @DisplayName("PostRequest は非空ファイルがありコメント未指定の場合に検証を通過する")
    void should_passValidation_when_imageFileIsPresentAndCommentIsNull() {
        final PostRequest request = new PostRequest();
        request.setImageFile(nonEmptyImageFile());
        request.setComment(null);

        final Set<ConstraintViolation<PostRequest>> violations = VALIDATOR.validate(request);

        assertNoViolations(violations);
    }

    @Test
    @DisplayName("PostRequest は imageFile が null の場合に検証エラーとなる")
    void should_failValidation_when_imageFileIsNull() {
        final PostRequest request = new PostRequest();
        request.setImageFile(null);
        request.setComment("caption");

        final Set<ConstraintViolation<PostRequest>> violations = VALIDATOR.validate(request);

        assertViolationsOnlyForProperty(violations, "imageFile");
    }

    @Test
    @DisplayName("PostRequest は空ファイルの場合に検証エラーとなる")
    void should_failValidation_when_imageFileIsEmpty() {
        final PostRequest request = new PostRequest();
        request.setImageFile(emptyImageFile());
        request.setComment("caption");

        final Set<ConstraintViolation<PostRequest>> violations = VALIDATOR.validate(request);

        assertViolationsOnlyForProperty(violations, "imageFile");
    }

    @Test
    @DisplayName("PostRequest は comment が100文字ちょうどの場合に検証を通過する")
    void should_passValidation_when_commentLengthIsMaxBoundary() {
        final PostRequest request = new PostRequest();
        request.setImageFile(nonEmptyImageFile());
        request.setComment("a".repeat(100));

        final Set<ConstraintViolation<PostRequest>> violations = VALIDATOR.validate(request);

        assertNoViolations(violations);
    }

    @Test
    @DisplayName("PostRequest は comment が101文字の場合に検証エラーとなる")
    void should_failValidation_when_commentIsTooLong() {
        final PostRequest request = new PostRequest();
        request.setImageFile(nonEmptyImageFile());
        request.setComment("a".repeat(101));

        final Set<ConstraintViolation<PostRequest>> violations = VALIDATOR.validate(request);

        assertViolationsOnlyForProperty(violations, "comment");
    }

    @Test
    @DisplayName("PostRequest は comment に制御文字を含む場合に検証エラーとなる")
    void should_failValidation_when_commentContainsControlCharacters() {
        final PostRequest request = new PostRequest();
        request.setImageFile(nonEmptyImageFile());
        request.setComment("abc\u0000");

        final Set<ConstraintViolation<PostRequest>> violations = VALIDATOR.validate(request);

        assertViolationsOnlyForProperty(violations, "comment");
    }

    private static MockMultipartFile nonEmptyImageFile() {
        return new MockMultipartFile("imageFile", "post.jpg", "image/jpeg", new byte[] {1});
    }

    private static MockMultipartFile emptyImageFile() {
        return new MockMultipartFile("imageFile", "post.jpg", "image/jpeg", new byte[0]);
    }

}
