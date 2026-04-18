package jp.i432kg.footprint.application.port;

import jp.i432kg.footprint.domain.value.ImageId;

/**
 * {@link ImageId} を生成するためのアプリケーション境界です。
 */
@FunctionalInterface
public interface ImageIdGenerator {

    /**
     * {@link ImageId} を生成します。
     *
     * @return 生成した {@link ImageId}
     */
    ImageId generate();
}
