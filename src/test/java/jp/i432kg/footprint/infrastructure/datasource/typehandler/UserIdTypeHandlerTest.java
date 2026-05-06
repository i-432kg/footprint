package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.value.UserId;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserIdTypeHandlerTest extends AbstractTypeHandlerTestSupport {

    private final UserIdTypeHandler handler = new UserIdTypeHandler();

    @Test
    @DisplayName("UserIdTypeHandler.setNonNullParameter は UserId の値を文字列として設定する")
    void should_setStringValue_when_settingNonNullParameter() throws Exception {
        final UserId userId = DomainTestFixtures.userId();

        handler.setNonNullParameter(preparedStatement, 1, userId, JdbcType.VARCHAR);

        verify(preparedStatement).setString(1, userId.getValue());
    }

    @Test
    @DisplayName("UserIdTypeHandler.getNullableResult は DB 文字列から UserId を復元する")
    void should_returnUserId_when_gettingNullableResult() throws Exception {
        when(resultSet.getString("user_id")).thenReturn(DomainTestFixtures.USER_ID);
        when(resultSet.getString(1)).thenReturn(DomainTestFixtures.USER_ID);
        when(callableStatement.getString(1)).thenReturn(DomainTestFixtures.USER_ID);

        assertThat(handler.getNullableResult(resultSet, "user_id")).isEqualTo(DomainTestFixtures.userId());
        assertThat(handler.getNullableResult(resultSet, 1)).isEqualTo(DomainTestFixtures.userId());
        assertThat(handler.getNullableResult(callableStatement, 1)).isEqualTo(DomainTestFixtures.userId());
    }

    @Test
    @DisplayName("UserIdTypeHandler.getNullableResult は DB 値が null の場合に null を返す")
    void should_returnNull_when_databaseValueIsNull() throws Exception {
        when(resultSet.getString("user_id")).thenReturn(null);

        assertThat(handler.getNullableResult(resultSet, "user_id")).isNull();
    }
}
