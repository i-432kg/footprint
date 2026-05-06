package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
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
     * 最小ピクセル数：0px
     */
    static int MIN_VALUE = 0;
    /**
     * 実用上の最大ピクセル数：100,000px
     */
    static int MAX_VALUE = 100_000;

    static String FIELD_NAME = "pixel";

    int value;

    /**
     * ピクセル値を指定して {@link Pixel} インスタンスを生成します。
     *
     * @param value ピクセル値
     * @return {@link Pixel} インスタンス
     * @throws InvalidValueException バリデーションエラーの場合
     */
    public static Pixel of(final int value) {

        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw InvalidValueException.outOfRange(FIELD_NAME, value, MIN_VALUE, MAX_VALUE);
        }

        return new Pixel(value);
    }
}
