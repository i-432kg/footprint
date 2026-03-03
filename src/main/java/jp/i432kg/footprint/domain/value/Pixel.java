package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * ピクセル単位の解像度を表す値オブジェクト。
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Pixel {

    int value;

    /**
     * ピクセル値を指定して {@link Pixel} インスタンスを生成します。
     *
     * @param value ピクセル値（0以上）
     * @return {@link Pixel} インスタンス
     * @throws IllegalArgumentException 負の値が指定された場合
     */
    public static Pixel of(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Pixel value cannot be negative: " + value);
        }
        return new Pixel(value);
    }

    /**
     * 値を取得します。
     *
     * @return ピクセル値
     */
    public int value() {
        return value;
    }
}