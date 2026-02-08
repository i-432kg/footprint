package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchKeyword {

    String value;

    public static SearchKeyword of(final String value) {
        return new SearchKeyword(value);
    }

    public String value() {
        return value;
    }
}
