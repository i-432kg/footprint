package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.value.Latitude;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LatitudeTypeHandlerTest extends AbstractTypeHandlerTestSupport {

    private final LatitudeTypeHandler handler = new LatitudeTypeHandler();

    @Test
    @DisplayName("LatitudeTypeHandler.setNonNullParameter は緯度を BigDecimal として設定する")
    void should_setBigDecimalValue_when_settingNonNullParameter() throws Exception {
        final Latitude latitude = DomainTestFixtures.latitude();

        handler.setNonNullParameter(preparedStatement, 1, latitude, JdbcType.DECIMAL);

        verify(preparedStatement).setBigDecimal(1, latitude.getValue());
    }

    @Test
    @DisplayName("LatitudeTypeHandler.getNullableResult は DB 数値から Latitude を復元する")
    void should_returnLatitude_when_gettingNullableResult() throws Exception {
        when(resultSet.getBigDecimal("latitude")).thenReturn(new BigDecimal("35.681236"));
        when(resultSet.wasNull()).thenReturn(false);
        when(resultSet.getBigDecimal(1)).thenReturn(new BigDecimal("35.681236"));
        when(callableStatement.getString(1)).thenReturn("35.681236");

        assertThat(handler.getNullableResult(resultSet, "latitude")).isEqualTo(DomainTestFixtures.latitude());
        assertThat(handler.getNullableResult(resultSet, 1)).isEqualTo(DomainTestFixtures.latitude());
        assertThat(handler.getNullableResult(callableStatement, 1)).isEqualTo(DomainTestFixtures.latitude());
    }

    @Test
    @DisplayName("LatitudeTypeHandler.getNullableResult は DB 値が null の場合に null を返す")
    void should_returnNull_when_databaseValueIsNull() throws Exception {
        when(resultSet.getBigDecimal("latitude")).thenReturn(null);
        when(resultSet.wasNull()).thenReturn(true);

        assertThat(handler.getNullableResult(resultSet, "latitude")).isNull();
    }
}
