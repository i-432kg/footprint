package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.FileName;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis の型ハンドラで FileName を String に変換するための実装クラス
 */
@MappedTypes(FileName.class)
public class FileNameTypeHandler extends BaseTypeHandler<FileName> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, FileName parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getValue());
    }

    @Override
    public FileName getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : FileName.of(value);
    }

    @Override
    public FileName getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : FileName.of(value);
    }

    @Override
    public FileName getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : FileName.of(value);
    }
}
