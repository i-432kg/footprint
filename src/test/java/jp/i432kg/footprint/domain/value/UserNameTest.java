package jp.i432kg.footprint.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.assertInvalidValue;
import static org.assertj.core.api.Assertions.assertThat;

class UserNameTest {

    @Test
    @DisplayName("UserName.of は前後空白を除去して保持する")
    void should_trimUserName_when_valueHasLeadingOrTrailingSpaces() {
        final UserName actual = UserName.of("  user_01  ");

        assertThat(actual.getValue()).isEqualTo("user_01");
    }

    @Test
    @DisplayName("UserName.of は長さ範囲内の ASCII 可視文字を受け入れる")
    void should_createUserName_when_valueLengthIsWithinRange() {
        assertThat(UserName.of("user").getValue()).isEqualTo("user");
        assertThat(UserName.of("a".repeat(15)).getValue()).isEqualTo("a".repeat(15));
    }

    @Test
    @DisplayName("UserName.of は null を拒否する")
    void should_throwException_when_userNameIsNull() {
        assertInvalidValue(() -> UserName.of(null), "username", "required");
    }

    @Test
    @DisplayName("UserName.of は長さ範囲外の値を拒否する")
    void should_throwException_when_userNameLengthIsOutOfRange() {
        assertInvalidValue(() -> UserName.of("abc"), "username", "out_of_range");
        assertInvalidValue(() -> UserName.of("a".repeat(16)), "username", "out_of_range");
    }

    @Test
    @DisplayName("UserName.of は空白や全角文字を含む値を拒否する")
    void should_throwException_when_userNameContainsUnsupportedCharacters() {
        assertInvalidValue(() -> UserName.of("ab c"), "username", "invalid_format");
        assertInvalidValue(() -> UserName.of("ユーザー"), "username", "invalid_format");
    }
}
