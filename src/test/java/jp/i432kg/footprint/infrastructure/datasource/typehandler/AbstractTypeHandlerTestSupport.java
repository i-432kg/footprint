package jp.i432kg.footprint.infrastructure.datasource.typehandler;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@ExtendWith(MockitoExtension.class)
abstract class AbstractTypeHandlerTestSupport {

    @Mock
    protected PreparedStatement preparedStatement;

    @Mock
    protected ResultSet resultSet;

    @Mock
    protected CallableStatement callableStatement;
}
