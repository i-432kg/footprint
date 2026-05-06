package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.value.ReplyComment;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReplyCommentTypeHandlerTest extends AbstractTypeHandlerTestSupport {

    private final ReplyCommentTypeHandler handler = new ReplyCommentTypeHandler();

    @Test
    @DisplayName("ReplyCommentTypeHandler.setNonNullParameter は返信コメントを文字列として設定する")
    void should_setStringValue_when_settingNonNullParameter() throws Exception {
        final ReplyComment replyComment = DomainTestFixtures.replyMessage();

        handler.setNonNullParameter(preparedStatement, 1, replyComment, JdbcType.VARCHAR);

        verify(preparedStatement).setString(1, replyComment.getValue());
    }

    @Test
    @DisplayName("ReplyCommentTypeHandler.getNullableResult は DB 文字列から ReplyComment を復元する")
    void should_returnReplyComment_when_gettingNullableResult() throws Exception {
        final String value = DomainTestFixtures.replyMessage().getValue();
        when(resultSet.getString("message")).thenReturn(value);
        when(resultSet.getString(1)).thenReturn(value);
        when(callableStatement.getString(1)).thenReturn(value);

        assertThat(handler.getNullableResult(resultSet, "message")).isEqualTo(DomainTestFixtures.replyMessage());
        assertThat(handler.getNullableResult(resultSet, 1)).isEqualTo(DomainTestFixtures.replyMessage());
        assertThat(handler.getNullableResult(callableStatement, 1)).isEqualTo(DomainTestFixtures.replyMessage());
    }

    @Test
    @DisplayName("ReplyCommentTypeHandler.getNullableResult は DB 値が null の場合に null を返す")
    void should_returnNull_when_databaseValueIsNull() throws Exception {
        when(resultSet.getString("message")).thenReturn(null);

        assertThat(handler.getNullableResult(resultSet, "message")).isNull();
    }
}
