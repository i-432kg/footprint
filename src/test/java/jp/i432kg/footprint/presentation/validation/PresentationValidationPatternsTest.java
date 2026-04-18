package jp.i432kg.footprint.presentation.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PresentationValidationPatternsTest {

    @Test
    @DisplayName("PresentationValidationPatterns.ULID は妥当な ULID にマッチする")
    void should_matchUlidPattern_when_valueIsValidUlid() {
        final boolean actual = "01ARZ3NDEKTSV4RRFFQ69G5FAV".matches(PresentationValidationPatterns.ULID);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("PresentationValidationPatterns.ULID は不正な ULID にマッチしない")
    void should_notMatchUlidPattern_when_valueIsInvalidUlid() {
        final boolean lowerCase = "01arz3ndektsv4rrffq69g5fav".matches(PresentationValidationPatterns.ULID);
        final boolean shortValue = "01ARZ3NDEKTSV4RRFFQ69G5FA".matches(PresentationValidationPatterns.ULID);

        assertThat(lowerCase).isFalse();
        assertThat(shortValue).isFalse();
    }

    @Test
    @DisplayName("PresentationValidationPatterns.NO_CONTROL_CHARS は通常文字列にマッチする")
    void should_matchNoControlCharsPattern_when_valueHasNoControlCharacters() {
        final boolean actual = "hello world".matches(PresentationValidationPatterns.NO_CONTROL_CHARS);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("PresentationValidationPatterns.NO_CONTROL_CHARS は制御文字を含む文字列にマッチしない")
    void should_notMatchNoControlCharsPattern_when_valueContainsControlCharacter() {
        final boolean actual = "abc\u0000".matches(PresentationValidationPatterns.NO_CONTROL_CHARS);

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("PresentationValidationPatterns.ASCII_VISIBLE_NO_SPACE は空白なし ASCII 可視文字にマッチする")
    void should_matchAsciiVisibleNoSpacePattern_when_valueHasVisibleAsciiWithoutSpaces() {
        final boolean actual = "Secret12!".matches(PresentationValidationPatterns.ASCII_VISIBLE_NO_SPACE);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("PresentationValidationPatterns.ASCII_VISIBLE_NO_SPACE は空白を含む文字列にマッチしない")
    void should_notMatchAsciiVisibleNoSpacePattern_when_valueContainsSpace() {
        final boolean actual = "Secret 12".matches(PresentationValidationPatterns.ASCII_VISIBLE_NO_SPACE);

        assertThat(actual).isFalse();
    }

}
