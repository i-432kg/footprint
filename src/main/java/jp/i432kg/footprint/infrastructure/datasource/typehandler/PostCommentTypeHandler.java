package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.PostComment;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;

/**
 * MyBatis の型ハンドラで {@link PostComment} を String に変換するための実装クラス
 */
@MappedTypes(PostComment.class)
public class PostCommentTypeHandler extends BaseTypeHandler<PostComment> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, PostComment parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getValue());
    }

    @Override
    public PostComment getNullableResult(ResultSet rs, String columnName) throws SQLException {
        final String value = rs.getString(columnName);
        return value == null ? null : PostComment.of(value);
    }

    @Override
    public PostComment getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        final String value = rs.getString(columnIndex);
        return value == null ? null : PostComment.of(value);
    }

    @Override
    public PostComment getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        final String value = cs.getString(columnIndex);
        return value == null ? null : PostComment.of(value);
    }
}
