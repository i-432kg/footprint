package jp.i432kg.footprint.domain.value;

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
     * 最大サイズ：5MB (5 * 1024 * 1024)
     */
    static long MAX_VALUE = 5_242_880;

    long value;

    /**
     * バイト値を指定して {@link Byte} インスタンスを生成します。
     *
     * @param value バイト値（0以上）
     * @return {@link Byte} インスタンス
     * @throws IllegalArgumentException バリデーションエラーの場合
     */
    public static Byte of(long value) {

        // 負の値を不許可
        if (value < 0) {
            throw new IllegalArgumentException("Byte size cannot be negative: " + value);
        }

        // 上限サイズ のチェック
        if (MAX_VALUE < value) {
            throw new IllegalArgumentException("Byte size exceeds the limit of 5MB: " + value);
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