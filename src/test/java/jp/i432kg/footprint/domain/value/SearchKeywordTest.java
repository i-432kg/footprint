package jp.i432kg.footprint.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.assertInvalidValue;
import static org.assertj.core.api.Assertions.assertThat;

class SearchKeywordTest {

    @Test
    @DisplayName("SearchKeyword.of は通常の検索キーワードを受け入れる")
    void should_createSearchKeyword_when_valueIsValid() {
        final SearchKeyword actual = SearchKeyword.of("camera bag");

        assertThat(actual.getValue()).isEqualTo("camera bag");
    }

    @Test
    @DisplayName("SearchKeyword.of は null または空白のみを拒否する")
    void should_throwException_when_searchKeywordIsNullOrBlank() {
        assertInvalidValue(() -> SearchKeyword.of(null), "search_keyword", "required");
        assertInvalidValue(() -> SearchKeyword.of(" "), "search_keyword", "blank");
    }

    @Test
    @DisplayName("SearchKeyword.of は最大長を超える値を拒否する")
    void should_throwException_when_searchKeywordExceedsMaxLength() {
        assertInvalidValue(() -> SearchKeyword.of("a".repeat(101)), "search_keyword", "too_long");
    }

    @Test
    @DisplayName("SearchKeyword.of は制御文字を含む値を拒否する")
    void should_throwException_when_searchKeywordContainsControlCharacters() {
        assertInvalidValue(() -> SearchKeyword.of("\n"), "search_keyword", "blank");
        assertInvalidValue(() -> SearchKeyword.of("\u0000"), "search_keyword", "invalid_format");
    }
}
