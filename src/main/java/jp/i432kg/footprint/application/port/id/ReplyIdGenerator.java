package jp.i432kg.footprint.application.port.id;

import jp.i432kg.footprint.domain.value.ReplyId;

/**
 * {@link ReplyId} を生成するためのアプリケーション境界です。
 */
@FunctionalInterface
public interface ReplyIdGenerator {

    /**
     * {@link ReplyId} を生成します。
     *
     * @return 生成した {@link ReplyId}
     */
    ReplyId generate();
}
