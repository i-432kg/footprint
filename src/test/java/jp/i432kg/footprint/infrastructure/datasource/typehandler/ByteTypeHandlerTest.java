package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.Byte;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ByteTypeHandlerTest extends AbstractTypeHandlerTestSupport {

    private final ByteTypeHandler handler = new ByteTypeHandler();

    @Test
    @DisplayName("ByteTypeHandler.setNonNullParameter はバイト数を long として設定する")
    void should_setLongValue_when_settingNonNullParameter() throws Exception {
        final Byte value = Byte.of(1024L);

        handler.setNonNullParameter(preparedStatement, 1, value, JdbcType.BIGINT);

        verify(preparedStatement).setLong(1, 1024L);
    }

    @Test
    @DisplayName("ByteTypeHandler.getNullableResult は DB 数値から Byte を復元する")
    void should_returnByteValue_when_gettingNullableResult() throws Exception {
        when(resultSet.getLong("size_bytes")).thenReturn(1024L);
        when(resultSet.wasNull()).thenReturn(false);
        when(resultSet.getLong(1)).thenReturn(1024L);
        when(callableStatement.getLong(1)).thenReturn(1024L);
        when(callableStatement.wasNull()).thenReturn(false);

        assertThat(handler.getNullableResult(resultSet, "size_bytes")).isEqualTo(Byte.of(1024L));
        assertThat(handler.getNullableResult(resultSet, 1)).isEqualTo(Byte.of(1024L));
        assertThat(handler.getNullableResult(callableStatement, 1)).isEqualTo(Byte.of(1024L));
    }

    @Test
    @DisplayName("ByteTypeHandler.getNullableResult は wasNull が true の場合に null を返す")
    void should_returnNull_when_wasNullIsTrue() throws Exception {
        when(resultSet.getLong("size_bytes")).thenReturn(0L);
        when(resultSet.wasNull()).thenReturn(true);

        assertThat(handler.getNullableResult(resultSet, "size_bytes")).isNull();
    }
}
