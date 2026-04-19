package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.BirthDate;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BirthDateTypeHandlerTest extends AbstractTypeHandlerTestSupport {

    private final BirthDateTypeHandler handler = new BirthDateTypeHandler();

    @Test
    @DisplayName("BirthDateTypeHandler.setNonNullParameter は生年月日を LocalDate として設定する")
    void should_setLocalDateValue_when_settingNonNullParameter() throws Exception {
        final BirthDate birthDate = BirthDate.restore(LocalDate.of(2000, 1, 1));

        handler.setNonNullParameter(preparedStatement, 1, birthDate, JdbcType.DATE);

        verify(preparedStatement).setObject(1, LocalDate.of(2000, 1, 1));
    }

    @Test
    @DisplayName("BirthDateTypeHandler.getNullableResult は DB 日付から BirthDate を復元する")
    void should_returnBirthDate_when_gettingNullableResult() throws Exception {
        final LocalDate value = LocalDate.of(2000, 1, 1);
        when(resultSet.getObject("birthdate", LocalDate.class)).thenReturn(value);
        when(resultSet.getObject(1, LocalDate.class)).thenReturn(value);
        when(callableStatement.getObject(1, LocalDate.class)).thenReturn(value);

        assertThat(handler.getNullableResult(resultSet, "birthdate")).isEqualTo(BirthDate.restore(value));
        assertThat(handler.getNullableResult(resultSet, 1)).isEqualTo(BirthDate.restore(value));
        assertThat(handler.getNullableResult(callableStatement, 1)).isEqualTo(BirthDate.restore(value));
    }

    @Test
    @DisplayName("BirthDateTypeHandler.getNullableResult は DB 値が null の場合に null を返す")
    void should_returnNull_when_databaseValueIsNull() throws Exception {
        when(resultSet.getObject("birthdate", LocalDate.class)).thenReturn(null);

        assertThat(handler.getNullableResult(resultSet, "birthdate")).isNull();
    }
}
