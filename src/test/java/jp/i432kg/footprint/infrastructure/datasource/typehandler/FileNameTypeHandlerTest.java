package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.FileName;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FileNameTypeHandlerTest extends AbstractTypeHandlerTestSupport {

    private final FileNameTypeHandler handler = new FileNameTypeHandler();

    @Test
    @DisplayName("FileNameTypeHandler.setNonNullParameter はファイル名を文字列として設定する")
    void should_setStringValue_when_settingNonNullParameter() throws Exception {
        final FileName fileName = FileName.of("image.jpg");

        handler.setNonNullParameter(preparedStatement, 1, fileName, JdbcType.VARCHAR);

        verify(preparedStatement).setString(1, "image.jpg");
    }

    @Test
    @DisplayName("FileNameTypeHandler.getNullableResult は DB 文字列から FileName を復元する")
    void should_returnFileName_when_gettingNullableResult() throws Exception {
        when(resultSet.getString("file_name")).thenReturn("image.jpg");
        when(resultSet.getString(1)).thenReturn("image.jpg");
        when(callableStatement.getString(1)).thenReturn("image.jpg");

        assertThat(handler.getNullableResult(resultSet, "file_name")).isEqualTo(FileName.of("image.jpg"));
        assertThat(handler.getNullableResult(resultSet, 1)).isEqualTo(FileName.of("image.jpg"));
        assertThat(handler.getNullableResult(callableStatement, 1)).isEqualTo(FileName.of("image.jpg"));
    }

    @Test
    @DisplayName("FileNameTypeHandler.getNullableResult は DB 値が null の場合に null を返す")
    void should_returnNull_when_databaseValueIsNull() throws Exception {
        when(resultSet.getString("file_name")).thenReturn(null);

        assertThat(handler.getNullableResult(resultSet, "file_name")).isNull();
    }
}
