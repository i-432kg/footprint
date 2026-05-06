package jp.i432kg.footprint.logging.operation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * handler method が表す operation 名を宣言するアノテーションです。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogOperation {

    /**
     * request 文脈へ設定する operation 名を返します。
     *
     * @return operation 名
     */
    String value();
}
