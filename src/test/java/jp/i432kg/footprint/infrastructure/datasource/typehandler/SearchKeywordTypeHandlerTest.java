package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.SearchKeyword;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SearchKeywordTypeHandlerTest extends AbstractTypeHandlerTestSupport {

    private final SearchKeywordTypeHandler handler = new SearchKeywordTypeHandler();

    @Test
    @DisplayName("SearchKeywordTypeHandler.setNonNullParameter は検索キーワードを文字列として設定する")
    void should_setStringValue_when_settingNonNullParameter() throws Exception {
        final SearchKeyword keyword = SearchKeyword.of("hello");

        handler.setNonNullParameter(preparedStatement, 1, keyword, JdbcType.VARCHAR);

        verify(preparedStatement).setString(1, "hello");
    }

    @Test
    @DisplayName("SearchKeywordTypeHandler.getNullableResult は DB 文字列から SearchKeyword を復元する")
    void should_returnSearchKeyword_when_gettingNullableResult() throws Exception {
        when(resultSet.getString("keyword")).thenReturn("hello");
        when(resultSet.wasNull()).thenReturn(false);
        when(resultSet.getString(1)).thenReturn("hello");
        when(callableStatement.getString(1)).thenReturn("hello");
        when(callableStatement.wasNull()).thenReturn(false);

        assertThat(handler.getNullableResult(resultSet, "keyword")).isEqualTo(SearchKeyword.of("hello"));
        assertThat(handler.getNullableResult(resultSet, 1)).isEqualTo(SearchKeyword.of("hello"));
        assertThat(handler.getNullableResult(callableStatement, 1)).isEqualTo(SearchKeyword.of("hello"));
    }

    @Test
    @DisplayName("SearchKeywordTypeHandler.getNullableResult は DB 値が null の場合に null を返す")
    void should_returnNull_when_databaseValueIsNull() throws Exception {
        when(resultSet.getString("keyword")).thenReturn(null);
        when(resultSet.wasNull()).thenReturn(true);

        assertThat(handler.getNullableResult(resultSet, "keyword")).isNull();
    }
}
