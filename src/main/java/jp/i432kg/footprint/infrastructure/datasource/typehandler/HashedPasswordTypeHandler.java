package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.HashedPassword;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis の型ハンドラで HashedPassword を String に変換するための実装クラス
 */
@MappedTypes(HashedPassword.class)
public class HashedPasswordTypeHandler extends BaseTypeHandler<HashedPassword> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, HashedPassword parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.value());
    }

    @Override
    public HashedPassword getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : HashedPassword.of(value);
    }

    @Override
    public HashedPassword getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : HashedPassword.of(value);
    }

    @Override
    public HashedPassword getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : HashedPassword.of(value);
    }
}
