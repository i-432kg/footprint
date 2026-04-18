package jp.i432kg.footprint.application.port;

import jp.i432kg.footprint.domain.value.UserId;

/**
 * {@link UserId} を生成するためのアプリケーション境界です。
 */
@FunctionalInterface
public interface UserIdGenerator {

    /**
     * {@link UserId} を生成します。
     *
     * @return 生成した {@link UserId}
     */
    UserId generate();
}
