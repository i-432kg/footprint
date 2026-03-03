package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.Coordinate;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis の型ハンドラで Coordinate を double に変換するための実装クラス
 */
@MappedTypes(Coordinate.class)
public class CoordinateTypeHandler extends BaseTypeHandler<Coordinate> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Coordinate parameter, JdbcType jdbcType) throws SQLException {
        ps.setDouble(i, parameter.value());
    }

    @Override
    public Coordinate getNullableResult(ResultSet rs, String columnName) throws SQLException {
        double value = rs.getDouble(columnName);
        return rs.wasNull() ? null : Coordinate.of(value);
    }

    @Override
    public Coordinate getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        double value = rs.getDouble(columnIndex);
        return rs.wasNull() ? null : Coordinate.of(value);
    }

    @Override
    public Coordinate getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        double value = cs.getDouble(columnIndex);
        return cs.wasNull() ? null : Coordinate.of(value);
    }
}