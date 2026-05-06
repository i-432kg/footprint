package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.Pixel;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis の型ハンドラで Pixel を int に変換するための実装クラス
 */
@MappedTypes(Pixel.class)
public class PixelTypeHandler extends BaseTypeHandler<Pixel> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Pixel parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public Pixel getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : Pixel.of(value);
    }

    @Override
    public Pixel getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int value = rs.getInt(columnIndex);
        return rs.wasNull() ? null : Pixel.of(value);
    }

    @Override
    public Pixel getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int value = cs.getInt(columnIndex);
        return cs.wasNull() ? null : Pixel.of(value);
    }
}
