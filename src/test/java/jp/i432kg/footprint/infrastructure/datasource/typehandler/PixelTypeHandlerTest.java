package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.Pixel;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PixelTypeHandlerTest extends AbstractTypeHandlerTestSupport {

    private final PixelTypeHandler handler = new PixelTypeHandler();

    @Test
    @DisplayName("PixelTypeHandler.setNonNullParameter はピクセル数を int として設定する")
    void should_setIntValue_when_settingNonNullParameter() throws Exception {
        final Pixel pixel = Pixel.of(640);

        handler.setNonNullParameter(preparedStatement, 1, pixel, JdbcType.INTEGER);

        verify(preparedStatement).setInt(1, 640);
    }

    @Test
    @DisplayName("PixelTypeHandler.getNullableResult は DB 数値から Pixel を復元する")
    void should_returnPixel_when_gettingNullableResult() throws Exception {
        when(resultSet.getInt("width")).thenReturn(640);
        when(resultSet.wasNull()).thenReturn(false);
        when(resultSet.getInt(1)).thenReturn(640);
        when(callableStatement.getInt(1)).thenReturn(640);
        when(callableStatement.wasNull()).thenReturn(false);

        assertThat(handler.getNullableResult(resultSet, "width")).isEqualTo(Pixel.of(640));
        assertThat(handler.getNullableResult(resultSet, 1)).isEqualTo(Pixel.of(640));
        assertThat(handler.getNullableResult(callableStatement, 1)).isEqualTo(Pixel.of(640));
    }

    @Test
    @DisplayName("PixelTypeHandler.getNullableResult は wasNull が true の場合に null を返す")
    void should_returnNull_when_wasNullIsTrue() throws Exception {
        when(resultSet.getInt("width")).thenReturn(0);
        when(resultSet.wasNull()).thenReturn(true);

        assertThat(handler.getNullableResult(resultSet, "width")).isNull();
    }
}
