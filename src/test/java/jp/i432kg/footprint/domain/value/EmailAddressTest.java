package jp.i432kg.footprint.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.assertInvalidValue;
import static org.assertj.core.api.Assertions.assertThat;

class EmailAddressTest {

    @Test
    @DisplayName("EmailAddress.of は前後空白を除去し小文字化した値を保持する")
    void should_normalizeEmailAddress_when_valueHasSpacesAndUpperCase() {
        final EmailAddress actual = EmailAddress.of("  Foo.Bar+1@Example.COM  ");

        assertThat(actual.getValue()).isEqualTo("foo.bar+1@example.com");
    }

    @Test
    @DisplayName("EmailAddress.of は妥当なメールアドレス形式を受け入れる")
    void should_createEmailAddress_when_valueHasValidFormat() {
        final EmailAddress actual = EmailAddress.of("user.name@example.co.jp");

        assertThat(actual.getValue()).isEqualTo("user.name@example.co.jp");
    }

    @Test
    @DisplayName("EmailAddress.of は null を拒否する")
    void should_throwException_when_emailAddressIsNull() {
        assertInvalidValue(() -> EmailAddress.of(null), "email", "required");
    }

    @Test
    @DisplayName("EmailAddress.of は空白のみを拒否する")
    void should_throwException_when_emailAddressIsBlank() {
        assertInvalidValue(() -> EmailAddress.of("   "), "   ", "blank");
    }

    @Test
    @DisplayName("EmailAddress.of は最大長を超える値を拒否する")
    void should_throwException_when_emailAddressExceedsMaxLength() {
        final String localPart = "a".repeat(64);
        final String domainPart = "b".repeat(189) + ".com";

        assertInvalidValue(
                () -> EmailAddress.of(localPart + "@" + domainPart),
                "email",
                "too_long"
        );
    }

    @Test
    @DisplayName("EmailAddress.of は @ の個数が不正な値を拒否する")
    void should_throwException_when_emailAddressHasInvalidAtCount() {
        assertInvalidValue(() -> EmailAddress.of("abc.example.com"), "email", "invalid_format");
        assertInvalidValue(() -> EmailAddress.of("a@b@c.com"), "email", "invalid_format");
    }

    @Test
    @DisplayName("EmailAddress.of は不正なローカル部を拒否する")
    void should_throwException_when_emailLocalPartIsInvalid() {
        assertInvalidValue(() -> EmailAddress.of("@example.com"), "email_local_part", "blank");
        assertInvalidValue(() -> EmailAddress.of(".user@example.com"), "email_local_part", "cannot start or end with a dot");
        assertInvalidValue(() -> EmailAddress.of("user.@example.com"), "email_local_part", "cannot start or end with a dot");
        assertInvalidValue(() -> EmailAddress.of("us..er@example.com"), "email_local_part", "cannot start or end with a dot");
        assertInvalidValue(() -> EmailAddress.of("a".repeat(65) + "@example.com"), "email_local_part", "too_long");
        assertInvalidValue(() -> EmailAddress.of("user()@example.com"), "email_local_part", "invalid_format");
    }

    @Test
    @DisplayName("EmailAddress.of は不正なドメイン部を拒否する")
    void should_throwException_when_emailDomainPartIsInvalid() {
        assertInvalidValue(() -> EmailAddress.of("user@example"), "email_domain_part", "must contain at least one dot");
        assertInvalidValue(() -> EmailAddress.of("user@example..com"), "email_domain_part", "must contain at least one dot");
        assertInvalidValue(() -> EmailAddress.of("user@.example.com"), "email_domain_part", "must contain at least one dot");
        assertInvalidValue(() -> EmailAddress.of("user@example.com."), "email_domain_part", "must contain at least one dot");
        assertInvalidValue(() -> EmailAddress.of("user@exam_ple.com"), "email_domain_part", "invalid_format");
    }
}
