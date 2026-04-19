package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.value.PostComment;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostCommentTypeHandlerTest extends AbstractTypeHandlerTestSupport {

    private final PostCommentTypeHandler handler = new PostCommentTypeHandler();

    @Test
    @DisplayName("PostCommentTypeHandler.setNonNullParameter は投稿コメントを文字列として設定する")
    void should_setStringValue_when_settingNonNullParameter() throws Exception {
        final PostComment postComment = DomainTestFixtures.caption();

        handler.setNonNullParameter(preparedStatement, 1, postComment, JdbcType.VARCHAR);

        verify(preparedStatement).setString(1, postComment.getValue());
    }

    @Test
    @DisplayName("PostCommentTypeHandler.getNullableResult は DB 文字列から PostComment を復元する")
    void should_returnPostComment_when_gettingNullableResult() throws Exception {
        final String value = DomainTestFixtures.caption().getValue();
        when(resultSet.getString("caption")).thenReturn(value);
        when(resultSet.getString(1)).thenReturn(value);
        when(callableStatement.getString(1)).thenReturn(value);

        assertThat(handler.getNullableResult(resultSet, "caption")).isEqualTo(DomainTestFixtures.caption());
        assertThat(handler.getNullableResult(resultSet, 1)).isEqualTo(DomainTestFixtures.caption());
        assertThat(handler.getNullableResult(callableStatement, 1)).isEqualTo(DomainTestFixtures.caption());
    }

    @Test
    @DisplayName("PostCommentTypeHandler.getNullableResult は DB 値が null の場合に null を返す")
    void should_returnNull_when_databaseValueIsNull() throws Exception {
        when(resultSet.getString("caption")).thenReturn(null);

        assertThat(handler.getNullableResult(resultSet, "caption")).isNull();
    }
}
