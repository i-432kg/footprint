package jp.i432kg.footprint.presentation.helper;

import jp.i432kg.footprint.domain.value.SearchKeyword;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * リクエストパラメータの文字列を SearchKeyword に変換する Converter
 */
@Component
public class SearchKeywordConverter implements Converter<String, SearchKeyword> {

    @Override
    public SearchKeyword convert(final String source) {
        return Objects.isNull(source) || source.isBlank() ? null : SearchKeyword.of(source);
    }
}