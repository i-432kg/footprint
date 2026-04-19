package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.UserName;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserNameTypeHandlerTest extends AbstractTypeHandlerTestSupport {

    private final UserNameTypeHandler handler = new UserNameTypeHandler();

    @Test
    @DisplayName("UserNameTypeHandler.setNonNullParameter はユーザー名を文字列として設定する")
    void should_setStringValue_when_settingNonNullParameter() throws Exception {
        final UserName userName = UserName.of("user_01");

        handler.setNonNullParameter(preparedStatement, 1, userName, JdbcType.VARCHAR);

        verify(preparedStatement).setString(1, "user_01");
    }

    @Test
    @DisplayName("UserNameTypeHandler.getNullableResult は DB 文字列から UserName を復元する")
    void should_returnUserName_when_gettingNullableResult() throws Exception {
        when(resultSet.getString("username")).thenReturn("user_01");
        when(resultSet.getString(1)).thenReturn("user_01");
        when(callableStatement.getString(1)).thenReturn("user_01");

        assertThat(handler.getNullableResult(resultSet, "username")).isEqualTo(UserName.of("user_01"));
        assertThat(handler.getNullableResult(resultSet, 1)).isEqualTo(UserName.of("user_01"));
        assertThat(handler.getNullableResult(callableStatement, 1)).isEqualTo(UserName.of("user_01"));
    }

    @Test
    @DisplayName("UserNameTypeHandler.getNullableResult は DB 値が null の場合に null を返す")
    void should_returnNull_when_databaseValueIsNull() throws Exception {
        when(resultSet.getString("username")).thenReturn(null);

        assertThat(handler.getNullableResult(resultSet, "username")).isNull();
    }
}
