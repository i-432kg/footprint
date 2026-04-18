package jp.i432kg.footprint.infrastructure.id;

import com.github.f4b6a3.ulid.UlidCreator;
import jp.i432kg.footprint.application.port.ReplyIdGenerator;
import jp.i432kg.footprint.domain.value.ReplyId;
import org.springframework.stereotype.Component;

/**
 * ULID ベースで {@link ReplyId} を生成する実装です。
 */
@Component
public class UlidReplyIdGenerator implements ReplyIdGenerator {

    @Override
    public ReplyId generate() {
        return ReplyId.of(UlidCreator.getUlid().toString());
    }
}
