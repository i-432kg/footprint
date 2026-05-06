package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.value.ObjectKey;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ObjectKeyTypeHandlerTest extends AbstractTypeHandlerTestSupport {

    private final ObjectKeyTypeHandler handler = new ObjectKeyTypeHandler();

    @Test
    @DisplayName("ObjectKeyTypeHandler.setNonNullParameter は object key を文字列として設定する")
    void should_setStringValue_when_settingNonNullParameter() throws Exception {
        final ObjectKey objectKey = DomainTestFixtures.objectKey();

        handler.setNonNullParameter(preparedStatement, 1, objectKey, JdbcType.VARCHAR);

        verify(preparedStatement).setString(1, objectKey.getValue());
    }

    @Test
    @DisplayName("ObjectKeyTypeHandler.getNullableResult は DB 文字列から ObjectKey を復元する")
    void should_returnObjectKey_when_gettingNullableResult() throws Exception {
        final String value = DomainTestFixtures.objectKey().getValue();
        when(resultSet.getString("object_key")).thenReturn(value);
        when(resultSet.getString(1)).thenReturn(value);
        when(callableStatement.getString(1)).thenReturn(value);

        assertThat(handler.getNullableResult(resultSet, "object_key")).isEqualTo(DomainTestFixtures.objectKey());
        assertThat(handler.getNullableResult(resultSet, 1)).isEqualTo(DomainTestFixtures.objectKey());
        assertThat(handler.getNullableResult(callableStatement, 1)).isEqualTo(DomainTestFixtures.objectKey());
    }

    @Test
    @DisplayName("ObjectKeyTypeHandler.getNullableResult は DB 値が null の場合に null を返す")
    void should_returnNull_when_databaseValueIsNull() throws Exception {
        when(resultSet.getString("object_key")).thenReturn(null);

        assertThat(handler.getNullableResult(resultSet, "object_key")).isNull();
    }
}
