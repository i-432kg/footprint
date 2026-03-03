package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * ファイル名を表す値オブジェクト。
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FileName {

    String value;

    /**
     * ファイル名を指定して {@link FileName} インスタンスを生成します。
     *
     * @param value ファイル名
     * @return {@link FileName} インスタンス
     * @throws IllegalArgumentException 引数が null または空文字の場合
     */
    public static FileName of(final String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
        return new FileName(value);
    }

    public String value() {
        return value;
    }
}