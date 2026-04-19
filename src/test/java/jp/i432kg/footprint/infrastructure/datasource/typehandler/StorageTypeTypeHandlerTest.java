package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.StorageType;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StorageTypeTypeHandlerTest extends AbstractTypeHandlerTestSupport {

    private final StorageTypeTypeHandler handler = new StorageTypeTypeHandler();

    @Test
    @DisplayName("StorageTypeTypeHandler.setNonNullParameter は保存種別を文字列として設定する")
    void should_setStringValue_when_settingNonNullParameter() throws Exception {
        handler.setNonNullParameter(preparedStatement, 1, StorageType.LOCAL, JdbcType.VARCHAR);

        verify(preparedStatement).setString(1, StorageType.LOCAL.getValue());
    }

    @Test
    @DisplayName("StorageTypeTypeHandler.getNullableResult は DB 文字列から StorageType を復元する")
    void should_returnStorageType_when_gettingNullableResult() throws Exception {
        when(resultSet.getString("storage_type")).thenReturn("LOCAL");
        when(resultSet.getString(1)).thenReturn("LOCAL");
        when(callableStatement.getString(1)).thenReturn("LOCAL");

        assertThat(handler.getNullableResult(resultSet, "storage_type")).isEqualTo(StorageType.LOCAL);
        assertThat(handler.getNullableResult(resultSet, 1)).isEqualTo(StorageType.LOCAL);
        assertThat(handler.getNullableResult(callableStatement, 1)).isEqualTo(StorageType.LOCAL);
    }

    @Test
    @DisplayName("StorageTypeTypeHandler.getNullableResult は DB 値が null の場合に null を返す")
    void should_returnNull_when_databaseValueIsNull() throws Exception {
        when(resultSet.getString("storage_type")).thenReturn(null);

        assertThat(handler.getNullableResult(resultSet, "storage_type")).isNull();
    }
}
