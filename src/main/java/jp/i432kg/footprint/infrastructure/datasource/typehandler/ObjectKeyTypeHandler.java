package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.ObjectKey;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis の型ハンドラで ObjectKey を String に変換するための実装クラス
 */
@MappedTypes(ObjectKey.class)
public class ObjectKeyTypeHandler extends BaseTypeHandler<ObjectKey> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ObjectKey parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getValue());
    }

    @Override
    public ObjectKey getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : ObjectKey.of(value);
    }

    @Override
    public ObjectKey getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : ObjectKey.of(value);
    }

    @Override
    public ObjectKey getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : ObjectKey.of(value);
    }
}
