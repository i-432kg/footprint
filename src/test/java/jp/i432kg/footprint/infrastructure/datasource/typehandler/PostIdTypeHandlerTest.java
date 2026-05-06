package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.value.PostId;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostIdTypeHandlerTest extends AbstractTypeHandlerTestSupport {

    private final PostIdTypeHandler handler = new PostIdTypeHandler();

    @Test
    @DisplayName("PostIdTypeHandler.setNonNullParameter は PostId の値を文字列として設定する")
    void should_setStringValue_when_settingNonNullParameter() throws Exception {
        final PostId postId = DomainTestFixtures.postId();

        handler.setNonNullParameter(preparedStatement, 1, postId, JdbcType.VARCHAR);

        verify(preparedStatement).setString(1, postId.getValue());
    }

    @Test
    @DisplayName("PostIdTypeHandler.getNullableResult は列名指定で PostId を復元する")
    void should_returnPostId_when_gettingNullableResultByColumnName() throws Exception {
        when(resultSet.getString("post_id")).thenReturn(DomainTestFixtures.POST_ID);

        final PostId actual = handler.getNullableResult(resultSet, "post_id");

        assertThat(actual).isEqualTo(DomainTestFixtures.postId());
    }

    @Test
    @DisplayName("PostIdTypeHandler.getNullableResult は列番号指定で PostId を復元する")
    void should_returnPostId_when_gettingNullableResultByColumnIndex() throws Exception {
        when(resultSet.getString(1)).thenReturn(DomainTestFixtures.POST_ID);

        final PostId actual = handler.getNullableResult(resultSet, 1);

        assertThat(actual).isEqualTo(DomainTestFixtures.postId());
    }

    @Test
    @DisplayName("PostIdTypeHandler.getNullableResult は CallableStatement から PostId を復元する")
    void should_returnPostId_when_gettingNullableResultFromCallableStatement() throws Exception {
        when(callableStatement.getString(1)).thenReturn(DomainTestFixtures.POST_ID);

        final PostId actual = handler.getNullableResult(callableStatement, 1);

        assertThat(actual).isEqualTo(DomainTestFixtures.postId());
    }

    @Test
    @DisplayName("PostIdTypeHandler.getNullableResult は DB 値が null の場合に null を返す")
    void should_returnNull_when_databaseValueIsNull() throws Exception {
        when(resultSet.getString("post_id")).thenReturn(null);

        final PostId actual = handler.getNullableResult(resultSet, "post_id");

        assertThat(actual).isNull();
    }
}
