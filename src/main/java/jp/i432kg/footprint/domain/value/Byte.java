package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * データサイズ（バイト単位）を表す値オブジェクト。
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Byte {

    long value;

    /**
     * バイト値を指定して {@link Byte} インスタンスを生成します。
     *
     * @param value バイト値（0以上）
     * @return {@link Byte} インスタンス
     * @throws IllegalArgumentException 負の値が指定された場合
     */
    public static Byte of(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Byte size cannot be negative: " + value);
        }
        return new Byte(value);
    }

    /**
     * 値を取得します。
     *
     * @return バイト値
     */
    public long value() {
        return value;
    }
}