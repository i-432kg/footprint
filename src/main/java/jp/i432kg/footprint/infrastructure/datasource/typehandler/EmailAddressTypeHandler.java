package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jp.i432kg.footprint.domain.value.EmailAddress;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

/**
 * MyBatis の型ハンドラで EmailAddress を String に変換するための実装クラス
 */
@MappedTypes(EmailAddress.class)
public class EmailAddressTypeHandler extends BaseTypeHandler<EmailAddress> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, EmailAddress parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getValue());
    }

    @Override
    public EmailAddress getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return rs.wasNull() ? null : EmailAddress.of(value);
    }

    @Override
    public EmailAddress getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return rs.wasNull() ? null : EmailAddress.of(value);
    }

    @Override
    public EmailAddress getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return cs.wasNull() ? null : EmailAddress.of(value);
    }
}
