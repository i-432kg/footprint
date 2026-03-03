package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.Byte;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis の型ハンドラで Byte を long に変換するための実装クラス
 */
@MappedTypes(Byte.class)
public class ByteTypeHandler extends BaseTypeHandler<Byte> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Byte parameter, JdbcType jdbcType) throws SQLException {
        ps.setLong(i, parameter.value());
    }

    @Override
    public Byte getNullableResult(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : Byte.of(value);
    }

    @Override
    public Byte getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        long value = rs.getLong(columnIndex);
        return rs.wasNull() ? null : Byte.of(value);
    }

    @Override
    public Byte getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        long value = cs.getLong(columnIndex);
        return cs.wasNull() ? null : Byte.of(value);
    }
}
