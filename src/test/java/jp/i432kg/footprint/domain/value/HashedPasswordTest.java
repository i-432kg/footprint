package jp.i432kg.footprint.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.assertInvalidValue;
import static org.assertj.core.api.Assertions.assertThat;

class HashedPasswordTest {

    @Test
    @DisplayName("HashedPassword.of は非空のハッシュ文字列を受け入れる")
    void should_createHashedPassword_when_valueIsNonBlank() {
        final HashedPassword actual = HashedPassword.of("$2a$10$abcdefghijklmnopqrstuv");

        assertThat(actual.getValue()).isEqualTo("$2a$10$abcdefghijklmnopqrstuv");
    }

    @Test
    @DisplayName("HashedPassword.of は null または空白のみを拒否する")
    void should_throwException_when_hashedPasswordIsNullOrBlank() {
        assertInvalidValue(() -> HashedPassword.of(null), "hashed_password", "required");
        assertInvalidValue(() -> HashedPassword.of(" "), "hashed_password", "blank");
    }

    @Test
    @DisplayName("HashedPassword.from は Hasher の戻り値から生成する")
    void should_createHashedPassword_when_createdFromRawPassword() {
        final RawPassword rawPassword = RawPassword.of("Secret12!");

        final HashedPassword actual = HashedPassword.from(rawPassword, value -> "hashed:" + value);

        assertThat(actual.getValue()).isEqualTo("hashed:Secret12!");
    }

    @Test
    @DisplayName("HashedPassword.from は Hasher の戻り値が不正な場合に失敗する")
    void should_throwException_when_hasherReturnsInvalidValue() {
        final RawPassword rawPassword = RawPassword.of("Secret12!");

        assertInvalidValue(() -> HashedPassword.from(rawPassword, value -> ""), "hashed_password", "blank");
        assertInvalidValue(() -> HashedPassword.from(rawPassword, value -> null), "hashed_password", "required");
    }
}
