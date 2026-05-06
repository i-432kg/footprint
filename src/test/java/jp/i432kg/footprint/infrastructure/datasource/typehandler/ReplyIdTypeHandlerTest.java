package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.value.ReplyId;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReplyIdTypeHandlerTest extends AbstractTypeHandlerTestSupport {

    private final ReplyIdTypeHandler handler = new ReplyIdTypeHandler();

    @Test
    @DisplayName("ReplyIdTypeHandler.setNonNullParameter は ReplyId の値を文字列として設定する")
    void should_setStringValue_when_settingNonNullParameter() throws Exception {
        final ReplyId replyId = DomainTestFixtures.replyId();

        handler.setNonNullParameter(preparedStatement, 1, replyId, JdbcType.VARCHAR);

        verify(preparedStatement).setString(1, replyId.getValue());
    }

    @Test
    @DisplayName("ReplyIdTypeHandler.getNullableResult は DB 文字列から ReplyId を復元する")
    void should_returnReplyId_when_gettingNullableResult() throws Exception {
        when(resultSet.getString("reply_id")).thenReturn(DomainTestFixtures.REPLY_ID);
        when(resultSet.getString(1)).thenReturn(DomainTestFixtures.REPLY_ID);
        when(callableStatement.getString(1)).thenReturn(DomainTestFixtures.REPLY_ID);

        assertThat(handler.getNullableResult(resultSet, "reply_id")).isEqualTo(DomainTestFixtures.replyId());
        assertThat(handler.getNullableResult(resultSet, 1)).isEqualTo(DomainTestFixtures.replyId());
        assertThat(handler.getNullableResult(callableStatement, 1)).isEqualTo(DomainTestFixtures.replyId());
    }

    @Test
    @DisplayName("ReplyIdTypeHandler.getNullableResult は DB 値が null の場合に null を返す")
    void should_returnNull_when_databaseValueIsNull() throws Exception {
        when(resultSet.getString("reply_id")).thenReturn(null);

        assertThat(handler.getNullableResult(resultSet, "reply_id")).isNull();
    }
}
