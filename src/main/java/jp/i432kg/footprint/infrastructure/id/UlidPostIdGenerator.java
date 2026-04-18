package jp.i432kg.footprint.infrastructure.id;

import com.github.f4b6a3.ulid.UlidCreator;
import jp.i432kg.footprint.application.port.PostIdGenerator;
import jp.i432kg.footprint.domain.value.PostId;
import org.springframework.stereotype.Component;

/**
 * ULID ベースで {@link PostId} を生成する実装です。
 */
@Component
public class UlidPostIdGenerator implements PostIdGenerator {

    @Override
    public PostId generate() {
        return PostId.of(UlidCreator.getUlid().toString());
    }
}
