package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.HashedPassword;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HashedPasswordTypeHandlerTest extends AbstractTypeHandlerTestSupport {

    private final HashedPasswordTypeHandler handler = new HashedPasswordTypeHandler();

    @Test
    @DisplayName("HashedPasswordTypeHandler.setNonNullParameter はハッシュ値を文字列として設定する")
    void should_setStringValue_when_settingNonNullParameter() throws Exception {
        final HashedPassword hashedPassword = HashedPassword.of("hashed-password");

        handler.setNonNullParameter(preparedStatement, 1, hashedPassword, JdbcType.VARCHAR);

        verify(preparedStatement).setString(1, "hashed-password");
    }

    @Test
    @DisplayName("HashedPasswordTypeHandler.getNullableResult は DB 文字列から HashedPassword を復元する")
    void should_returnHashedPassword_when_gettingNullableResult() throws Exception {
        when(resultSet.getString("password_hash")).thenReturn("hashed-password");
        when(resultSet.getString(1)).thenReturn("hashed-password");
        when(callableStatement.getString(1)).thenReturn("hashed-password");

        assertThat(handler.getNullableResult(resultSet, "password_hash")).isEqualTo(HashedPassword.of("hashed-password"));
        assertThat(handler.getNullableResult(resultSet, 1)).isEqualTo(HashedPassword.of("hashed-password"));
        assertThat(handler.getNullableResult(callableStatement, 1)).isEqualTo(HashedPassword.of("hashed-password"));
    }

    @Test
    @DisplayName("HashedPasswordTypeHandler.getNullableResult は DB 値が null の場合に null を返す")
    void should_returnNull_when_databaseValueIsNull() throws Exception {
        when(resultSet.getString("password_hash")).thenReturn(null);

        assertThat(handler.getNullableResult(resultSet, "password_hash")).isNull();
    }
}
