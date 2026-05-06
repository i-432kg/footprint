package jp.i432kg.footprint.presentation.api.request;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

class SignUpRequestTest extends RequestValidationTestSupport {

    @Test
    @DisplayName("SignUpRequest は正常な入力の場合に検証を通過する")
    void should_passValidation_when_allFieldsAreValid() {
        final SignUpRequest request = validRequest();

        final Set<ConstraintViolation<SignUpRequest>> violations = VALIDATOR.validate(request);

        assertNoViolations(violations);
    }

    @Test
    @DisplayName("SignUpRequest は userName が3文字の場合に検証エラーとなる")
    void should_failValidation_when_userNameIsTooShort() {
        final SignUpRequest request = validRequest();
        request.setUserName("abc");

        final Set<ConstraintViolation<SignUpRequest>> violations = VALIDATOR.validate(request);

        assertViolationsOnlyForProperty(violations, "userName");
    }

    @Test
    @DisplayName("SignUpRequest は userName に空白を含む場合に検証エラーとなる")
    void should_failValidation_when_userNameContainsSpace() {
        final SignUpRequest request = validRequest();
        request.setUserName("user name");

        final Set<ConstraintViolation<SignUpRequest>> violations = VALIDATOR.validate(request);

        assertViolationsOnlyForProperty(violations, "userName");
    }

    @Test
    @DisplayName("SignUpRequest は email が不正形式の場合に検証エラーとなる")
    void should_failValidation_when_emailFormatIsInvalid() {
        final SignUpRequest request = validRequest();
        request.setEmail("invalid");

        final Set<ConstraintViolation<SignUpRequest>> violations = VALIDATOR.validate(request);

        assertViolationsOnlyForProperty(violations, "email");
    }

    @Test
    @DisplayName("SignUpRequest は password が7文字の場合に検証エラーとなる")
    void should_failValidation_when_passwordIsTooShort() {
        final SignUpRequest request = validRequest();
        request.setPassword("Secret1");

        final Set<ConstraintViolation<SignUpRequest>> violations = VALIDATOR.validate(request);

        assertViolationsOnlyForProperty(violations, "password");
    }

    @Test
    @DisplayName("SignUpRequest は password に空白を含む場合に検証エラーとなる")
    void should_failValidation_when_passwordContainsSpace() {
        final SignUpRequest request = validRequest();
        request.setPassword("Secret 12");

        final Set<ConstraintViolation<SignUpRequest>> violations = VALIDATOR.validate(request);

        assertViolationsOnlyForProperty(violations, "password");
    }

    @Test
    @DisplayName("SignUpRequest は birthDate が当日の場合に検証を通過する")
    void should_passValidation_when_birthDateIsToday() {
        final SignUpRequest request = validRequest();
        request.setBirthDate(LocalDate.now());

        final Set<ConstraintViolation<SignUpRequest>> violations = VALIDATOR.validate(request);

        assertNoViolations(violations);
    }

    @Test
    @DisplayName("SignUpRequest は birthDate が未来日の場合に検証エラーとなる")
    void should_failValidation_when_birthDateIsFuture() {
        final SignUpRequest request = validRequest();
        request.setBirthDate(LocalDate.now().plusDays(1));

        final Set<ConstraintViolation<SignUpRequest>> violations = VALIDATOR.validate(request);

        assertViolationsOnlyForProperty(violations, "birthDate");
    }

    @Test
    @DisplayName("SignUpRequest は birthDate が null の場合に検証エラーとなる")
    void should_failValidation_when_birthDateIsNull() {
        final SignUpRequest request = validRequest();
        request.setBirthDate(null);

        final Set<ConstraintViolation<SignUpRequest>> violations = VALIDATOR.validate(request);

        assertViolationsOnlyForProperty(violations, "birthDate");
    }

    private static SignUpRequest validRequest() {
        final SignUpRequest request = new SignUpRequest();
        request.setUserName("user1234");
        request.setEmail("user@example.com");
        request.setPassword("Secret12");
        request.setBirthDate(LocalDate.of(2000, 1, 1));
        return request;
    }

}
