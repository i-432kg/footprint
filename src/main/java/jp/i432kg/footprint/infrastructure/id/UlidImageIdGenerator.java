package jp.i432kg.footprint.infrastructure.id;

import com.github.f4b6a3.ulid.UlidCreator;
import jp.i432kg.footprint.application.port.ImageIdGenerator;
import jp.i432kg.footprint.domain.value.ImageId;
import org.springframework.stereotype.Component;

/**
 * ULID ベースで {@link ImageId} を生成する実装です。
 */
@Component
public class UlidImageIdGenerator implements ImageIdGenerator {

    @Override
    public ImageId generate() {
        return ImageId.of(UlidCreator.getUlid().toString());
    }
}
