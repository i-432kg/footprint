package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jp.i432kg.footprint.domain.value.LoginId;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(LoginId.class)
public class LoginIdTypeHandler extends BaseTypeHandler<LoginId> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, LoginId parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.value());
    }

    @Override
    public LoginId getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return rs.wasNull() ? null : LoginId.of(value);
    }

    @Override
    public LoginId getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return rs.wasNull() ? null : LoginId.of(value);
    }

    @Override
    public LoginId getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return cs.wasNull() ? null : LoginId.of(value);
    }
}
