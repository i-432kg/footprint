package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SearchKeywordTest {

    @Test
    void of_shouldCreateInstance_whenValueIsValid() {
        SearchKeyword actual = SearchKeyword.of("tokyo station");

        assertThat(actual.getValue()).isEqualTo("tokyo station");
    }

    @Test
    void of_shouldRejectControlCharacters() {
        assertThatThrownBy(() -> SearchKeyword.of("abc\u0007def"))
                .isInstanceOf(InvalidValueException.class);
    }
}
