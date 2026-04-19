package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.FileExtension;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FileExtensionTypeHandlerTest extends AbstractTypeHandlerTestSupport {

    private final FileExtensionTypeHandler handler = new FileExtensionTypeHandler();

    @Test
    @DisplayName("FileExtensionTypeHandler.setNonNullParameter は拡張子を文字列として設定する")
    void should_setStringValue_when_settingNonNullParameter() throws Exception {
        final FileExtension fileExtension = FileExtension.of("jpg");

        handler.setNonNullParameter(preparedStatement, 1, fileExtension, JdbcType.VARCHAR);

        verify(preparedStatement).setString(1, "jpg");
    }

    @Test
    @DisplayName("FileExtensionTypeHandler.getNullableResult は DB 文字列から FileExtension を復元する")
    void should_returnFileExtension_when_gettingNullableResult() throws Exception {
        when(resultSet.getString("extension")).thenReturn("jpg");
        when(resultSet.getString(1)).thenReturn("jpg");
        when(callableStatement.getString(1)).thenReturn("jpg");

        assertThat(handler.getNullableResult(resultSet, "extension")).isEqualTo(FileExtension.of("jpg"));
        assertThat(handler.getNullableResult(resultSet, 1)).isEqualTo(FileExtension.of("jpg"));
        assertThat(handler.getNullableResult(callableStatement, 1)).isEqualTo(FileExtension.of("jpg"));
    }

    @Test
    @DisplayName("FileExtensionTypeHandler.getNullableResult は DB 値が null の場合に null を返す")
    void should_returnNull_when_databaseValueIsNull() throws Exception {
        when(resultSet.getString("extension")).thenReturn(null);

        assertThat(handler.getNullableResult(resultSet, "extension")).isNull();
    }
}
