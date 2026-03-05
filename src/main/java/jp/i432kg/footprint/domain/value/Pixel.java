package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * 画像の解像度（ピクセル）を表す値オブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Pixel {

    /**
     * 最大ピクセル数：8192px
     */
    private static final int MAX_VALUE = 8192;

    /**
     * 最小ピクセル数：320px
     */
    private static final int MIN_VALUE = 320;

    int value;

    /**
     * ピクセル値を指定して {@link Pixel} インスタンスを生成します。
     *
     * @param value ピクセル値
     * @return {@link Pixel} インスタンスバリデーションエラーの場合
     */
    public static Pixel of(final int value) {
        if (value < MIN_VALUE) {
            throw new IllegalArgumentException("Pixel value must be at least " + MIN_VALUE + "px: " + value);
        }

        if (value > MAX_VALUE) {
            throw new IllegalArgumentException("Pixel value exceeds the limit of " + MAX_VALUE + "px: " + value);
        }

        return new Pixel(value);
    }

    public int value() {
        return value;
    }
}