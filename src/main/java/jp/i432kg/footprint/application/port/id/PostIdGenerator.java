package jp.i432kg.footprint.application.port.id;

import jp.i432kg.footprint.domain.value.PostId;

/**
 * {@link PostId} を生成するためのアプリケーション境界です。
 */
@FunctionalInterface
public interface PostIdGenerator {

    /**
     * {@link PostId} を生成します。
     *
     * @return 生成した {@link PostId}
     */
    PostId generate();
}
