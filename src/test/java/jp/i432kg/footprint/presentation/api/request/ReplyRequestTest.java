package jp.i432kg.footprint.presentation.api.request;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

class ReplyRequestTest extends RequestValidationTestSupport {

    @Test
    @DisplayName("ReplyRequest は parentReplyId 未指定のルート返信で検証を通過する")
    void should_passValidation_when_parentReplyIdIsNull() {
        final ReplyRequest request = new ReplyRequest();
        request.setParentReplyId(null);
        request.setMessage("reply");

        final Set<ConstraintViolation<ReplyRequest>> violations = VALIDATOR.validate(request);

        assertNoViolations(violations);
    }

    @Test
    @DisplayName("ReplyRequest は ULID 形式の parentReplyId の場合に検証を通過する")
    void should_passValidation_when_parentReplyIdIsUlid() {
        final ReplyRequest request = new ReplyRequest();
        request.setParentReplyId("01ARZ3NDEKTSV4RRFFQ69G5FAV");
        request.setMessage("reply");

        final Set<ConstraintViolation<ReplyRequest>> violations = VALIDATOR.validate(request);

        assertNoViolations(violations);
    }

    @Test
    @DisplayName("ReplyRequest は parentReplyId が不正形式の場合に検証エラーとなる")
    void should_failValidation_when_parentReplyIdIsInvalidFormat() {
        final ReplyRequest request = new ReplyRequest();
        request.setParentReplyId("invalid");
        request.setMessage("reply");

        final Set<ConstraintViolation<ReplyRequest>> violations = VALIDATOR.validate(request);

        assertViolationsOnlyForProperty(violations, "parentReplyId");
    }

    @Test
    @DisplayName("ReplyRequest は message が blank の場合に検証エラーとなる")
    void should_failValidation_when_messageIsBlank() {
        final ReplyRequest request = new ReplyRequest();
        request.setParentReplyId(null);
        request.setMessage("   ");

        final Set<ConstraintViolation<ReplyRequest>> violations = VALIDATOR.validate(request);

        assertViolationsOnlyForProperty(violations, "message");
    }

    @Test
    @DisplayName("ReplyRequest は message が100文字ちょうどの場合に検証を通過する")
    void should_passValidation_when_messageLengthIsMaxBoundary() {
        final ReplyRequest request = new ReplyRequest();
        request.setParentReplyId(null);
        request.setMessage("a".repeat(100));

        final Set<ConstraintViolation<ReplyRequest>> violations = VALIDATOR.validate(request);

        assertNoViolations(violations);
    }

    @Test
    @DisplayName("ReplyRequest は message が101文字の場合に検証エラーとなる")
    void should_failValidation_when_messageIsTooLong() {
        final ReplyRequest request = new ReplyRequest();
        request.setParentReplyId(null);
        request.setMessage("a".repeat(101));

        final Set<ConstraintViolation<ReplyRequest>> violations = VALIDATOR.validate(request);

        assertViolationsOnlyForProperty(violations, "message");
    }

    @Test
    @DisplayName("ReplyRequest は message に制御文字を含む場合に検証エラーとなる")
    void should_failValidation_when_messageContainsControlCharacters() {
        final ReplyRequest request = new ReplyRequest();
        request.setParentReplyId(null);
        request.setMessage("abc\u0000");

        final Set<ConstraintViolation<ReplyRequest>> violations = VALIDATOR.validate(request);

        assertViolationsOnlyForProperty(violations, "message");
    }

}
