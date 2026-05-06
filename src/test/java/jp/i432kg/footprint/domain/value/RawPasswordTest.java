package jp.i432kg.footprint.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.assertInvalidValue;
import static org.assertj.core.api.Assertions.assertThat;

class RawPasswordTest {

    @Test
    @DisplayName("RawPassword.of は長さ範囲内の ASCII 可視文字を受け入れる")
    void should_createRawPassword_when_valueIsWithinLengthAndAsciiRange() {
        assertThat(RawPassword.of("Abc123!@").getValue()).isEqualTo("Abc123!@");
        assertThat(RawPassword.of("A".repeat(72)).getValue()).isEqualTo("A".repeat(72));
    }

    @Test
    @DisplayName("RawPassword.of は null または空白のみを拒否する")
    void should_throwException_when_rawPasswordIsNullOrBlank() {
        assertInvalidValue(() -> RawPassword.of(null), "password", "required");
        assertInvalidValue(() -> RawPassword.of(" "), "password", "blank");
    }

    @Test
    @DisplayName("RawPassword.of は長さ範囲外の値を拒否する")
    void should_throwException_when_rawPasswordLengthIsOutOfRange() {
        assertInvalidValue(() -> RawPassword.of("A".repeat(7)), "password", "out_of_range");
        assertInvalidValue(() -> RawPassword.of("A".repeat(73)), "password", "out_of_range");
    }

    @Test
    @DisplayName("RawPassword.of は ASCII 可視文字以外を拒否する")
    void should_throwException_when_rawPasswordContainsUnsupportedCharacters() {
        assertInvalidValue(() -> RawPassword.of("abc defg"), "password", "invalid_format");
        assertInvalidValue(() -> RawPassword.of("あいうえお123"), "password", "invalid_format");
    }

    @Test
    @DisplayName("RawPassword は toString で平文パスワードを露出しない")
    void should_maskValueInToString_when_rawPasswordIsCreated() {
        final RawPassword actual = RawPassword.of("Secret12!");

        assertThat(actual.toString()).contains("********");
        assertThat(actual.toString()).doesNotContain("Secret12!");
    }
}
