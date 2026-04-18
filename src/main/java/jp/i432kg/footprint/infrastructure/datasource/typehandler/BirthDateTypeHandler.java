package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.BirthDate;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * MyBatis の型ハンドラで BirthDate を LocalDate に変換するための実装クラス
 */
@MappedTypes(BirthDate.class)
public class BirthDateTypeHandler extends BaseTypeHandler<BirthDate> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, BirthDate parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter.getValue());
    }

    @Override
    public BirthDate getNullableResult(ResultSet rs, String columnName) throws SQLException {
        LocalDate value = rs.getObject(columnName, LocalDate.class);
        return value == null ? null : BirthDate.restore(value);
    }

    @Override
    public BirthDate getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        LocalDate value = rs.getObject(columnIndex, LocalDate.class);
        return value == null ? null : BirthDate.restore(value);
    }

    @Override
    public BirthDate getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        LocalDate value = cs.getObject(columnIndex, LocalDate.class);
        return value == null ? null : BirthDate.restore(value);
    }
}
