package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.ReplyComment;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;

/**
 * MyBatis の型ハンドラで {@link ReplyComment} を String に変換するための実装クラス
 */
@MappedTypes(ReplyComment.class)
public class ReplyCommentTypeHandler extends BaseTypeHandler<ReplyComment> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ReplyComment parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getValue());
    }

    @Override
    public ReplyComment getNullableResult(ResultSet rs, String columnName) throws SQLException {
        final String value = rs.getString(columnName);
        return value == null ? null : ReplyComment.of(value);
    }

    @Override
    public ReplyComment getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        final String value = rs.getString(columnIndex);
        return value == null ? null : ReplyComment.of(value);
    }

    @Override
    public ReplyComment getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        final String value = cs.getString(columnIndex);
        return value == null ? null : ReplyComment.of(value);
    }
}
