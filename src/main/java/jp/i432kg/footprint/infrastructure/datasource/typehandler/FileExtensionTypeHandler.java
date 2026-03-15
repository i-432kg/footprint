package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.FileExtension;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis の型ハンドラで FileExtension を String に変換するための実装クラス
 */
@MappedTypes(FileExtension.class)
public class FileExtensionTypeHandler extends BaseTypeHandler<FileExtension> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, FileExtension parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.value());
    }

    @Override
    public FileExtension getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : FileExtension.of(value);
    }

    @Override
    public FileExtension getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : FileExtension.of(value);
    }

    @Override
    public FileExtension getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : FileExtension.of(value);
    }
}
