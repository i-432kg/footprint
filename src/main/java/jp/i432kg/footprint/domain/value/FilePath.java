package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * ファイルの保存パスを表す値オブジェクト。
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FilePath {

    String value;

    /**
     * ファイルパスを指定して {@link FilePath} インスタンスを生成します。
     *
     * @param value ファイルパス
     * @return {@link FilePath} インスタンス
     * @throws IllegalArgumentException 引数が null または空文字の場合
     */
    public static FilePath of(final String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Image file path cannot be null or empty");
        }
        return new FilePath(value);
    }

    public String value() {
        return value;
    }
}