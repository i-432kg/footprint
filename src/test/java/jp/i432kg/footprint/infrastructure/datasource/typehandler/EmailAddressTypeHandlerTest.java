package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.EmailAddress;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailAddressTypeHandlerTest extends AbstractTypeHandlerTestSupport {

    private final EmailAddressTypeHandler handler = new EmailAddressTypeHandler();

    @Test
    @DisplayName("EmailAddressTypeHandler.setNonNullParameter はメールアドレスを文字列として設定する")
    void should_setStringValue_when_settingNonNullParameter() throws Exception {
        final EmailAddress emailAddress = EmailAddress.of("user@example.com");

        handler.setNonNullParameter(preparedStatement, 1, emailAddress, JdbcType.VARCHAR);

        verify(preparedStatement).setString(1, emailAddress.getValue());
    }

    @Test
    @DisplayName("EmailAddressTypeHandler.getNullableResult は DB 文字列から EmailAddress を復元する")
    void should_returnEmailAddress_when_gettingNullableResult() throws Exception {
        when(resultSet.getString("email")).thenReturn("user@example.com");
        when(resultSet.wasNull()).thenReturn(false);
        when(resultSet.getString(1)).thenReturn("user@example.com");
        when(callableStatement.getString(1)).thenReturn("user@example.com");
        when(callableStatement.wasNull()).thenReturn(false);

        assertThat(handler.getNullableResult(resultSet, "email")).isEqualTo(EmailAddress.of("user@example.com"));
        assertThat(handler.getNullableResult(resultSet, 1)).isEqualTo(EmailAddress.of("user@example.com"));
        assertThat(handler.getNullableResult(callableStatement, 1)).isEqualTo(EmailAddress.of("user@example.com"));
    }

    @Test
    @DisplayName("EmailAddressTypeHandler.getNullableResult は DB 値が null の場合に null を返す")
    void should_returnNull_when_databaseValueIsNull() throws Exception {
        when(resultSet.getString("email")).thenReturn(null);
        when(resultSet.wasNull()).thenReturn(true);

        assertThat(handler.getNullableResult(resultSet, "email")).isNull();
    }
}
