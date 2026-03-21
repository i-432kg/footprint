package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * データサイズ（バイト単位）を表す値オブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Byte {

    /**
     * 最小サイズ：0MB
     */
    static long MIN_VALUE = 0;

    /**
     * 最大サイズ：5MB (5 * 1024 * 1024)
     */
    static long MAX_VALUE = 5_242_880;

    static String FIELD_NAME = "byte";

    long value;

    /**
     * バイト値を指定して {@link Byte} インスタンスを生成します。
     *
     * @param value バイト値（0以上）
     * @return {@link Byte} インスタンス
     * @throws InvalidValueException バリデーションエラーの場合
     */
    public static Byte of(long value) {

        // サイズ上限下限のチェック
        if (value < MIN_VALUE || MAX_VALUE < value) {
            throw InvalidValueException.outOfRange(FIELD_NAME, value, MIN_VALUE, MAX_VALUE);
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