package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import jp.i432kg.footprint.domain.value.SearchKeyword;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(SearchKeyword.class)
public class SearchKeywordTypeHandler extends BaseTypeHandler<SearchKeyword> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, SearchKeyword parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.value());
    }

    @Override
    public SearchKeyword getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return rs.wasNull() ? null : SearchKeyword.of(value);
    }

    @Override
    public SearchKeyword getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return rs.wasNull() ? null : SearchKeyword.of(value);
    }

    @Override
    public SearchKeyword getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return cs.wasNull() ? null : SearchKeyword.of(value);
    }
}
