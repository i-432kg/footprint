package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.value.Longitude;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LongitudeTypeHandlerTest extends AbstractTypeHandlerTestSupport {

    private final LongitudeTypeHandler handler = new LongitudeTypeHandler();

    @Test
    @DisplayName("LongitudeTypeHandler.setNonNullParameter は経度を BigDecimal として設定する")
    void should_setBigDecimalValue_when_settingNonNullParameter() throws Exception {
        final Longitude longitude = DomainTestFixtures.longitude();

        handler.setNonNullParameter(preparedStatement, 1, longitude, JdbcType.DECIMAL);

        verify(preparedStatement).setBigDecimal(1, longitude.getValue());
    }

    @Test
    @DisplayName("LongitudeTypeHandler.getNullableResult は DB 数値から Longitude を復元する")
    void should_returnLongitude_when_gettingNullableResult() throws Exception {
        when(resultSet.getBigDecimal("longitude")).thenReturn(new BigDecimal("139.767125"));
        when(resultSet.wasNull()).thenReturn(false);
        when(resultSet.getBigDecimal(1)).thenReturn(new BigDecimal("139.767125"));
        when(callableStatement.getString(1)).thenReturn("139.767125");

        assertThat(handler.getNullableResult(resultSet, "longitude")).isEqualTo(DomainTestFixtures.longitude());
        assertThat(handler.getNullableResult(resultSet, 1)).isEqualTo(DomainTestFixtures.longitude());
        assertThat(handler.getNullableResult(callableStatement, 1)).isEqualTo(DomainTestFixtures.longitude());
    }

    @Test
    @DisplayName("LongitudeTypeHandler.getNullableResult は DB 値が null の場合に null を返す")
    void should_returnNull_when_databaseValueIsNull() throws Exception {
        when(resultSet.getBigDecimal("longitude")).thenReturn(null);
        when(resultSet.wasNull()).thenReturn(true);

        assertThat(handler.getNullableResult(resultSet, "longitude")).isNull();
    }
}
