package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.ReplyId;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis の型ハンドラで ReplyId を int に変換するための実装クラス
 */
@MappedTypes(ReplyId.class)
public class ReplyIdTypeHandler extends BaseTypeHandler<ReplyId> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ReplyId parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getValue());
    }

    @Override
    public ReplyId getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : ReplyId.of(value);
    }

    @Override
    public ReplyId getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : ReplyId.of(value);
    }

    @Override
    public ReplyId getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : ReplyId.of(value);
    }
}
