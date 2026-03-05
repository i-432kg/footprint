package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.Latitude;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis の型ハンドラで Latitude を BigDecimal に変換するための実装クラス
 */
@MappedTypes(Latitude.class)
public class LatitudeTypeHandler extends BaseTypeHandler<Latitude> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Latitude parameter, JdbcType jdbcType) throws SQLException {
        ps.setBigDecimal(i, parameter.value());
    }

    @Override
    public Latitude getNullableResult(ResultSet rs, String columnName) throws SQLException {
        BigDecimal value = rs.getBigDecimal(columnName);
        return rs.wasNull() ? null : Latitude.of(value);
    }

    @Override
    public Latitude getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        BigDecimal value = rs.getBigDecimal(columnIndex);
        return rs.wasNull() ? null : Latitude.of(value);
    }

    @Override
    public Latitude getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        BigDecimal value = cs.getString(columnIndex) != null ? new BigDecimal(cs.getString(columnIndex)) : null;
        return value == null ? null : Latitude.of(value);
    }
}
