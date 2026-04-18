package jp.i432kg.footprint.infrastructure.id;

import com.github.f4b6a3.ulid.UlidCreator;
import jp.i432kg.footprint.application.port.UserIdGenerator;
import jp.i432kg.footprint.domain.value.UserId;
import org.springframework.stereotype.Component;

/**
 * ULID ベースで {@link UserId} を生成する実装です。
 */
@Component
public class UlidUserIdGenerator implements UserIdGenerator {

    @Override
    public UserId generate() {
        return UserId.of(UlidCreator.getUlid().toString());
    }
}
