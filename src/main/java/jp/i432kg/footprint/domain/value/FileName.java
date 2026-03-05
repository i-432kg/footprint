package jp.i432kg.footprint.domain.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Objects;

/**
 * ファイル名を表す値オブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FileName {


    /**
     * ファイル名の最大文字数
     */
    private static final int MAX_LENGTH = 254;

    /**
     * 禁止文字の正規表現（OS/URL危険文字）
     * / \ : * ? " < > |
     */
    private static final String INVALID_CHARS_PATTERN = ".*[/\\\\:*?\"<>|].*";

    String value;

    /**
     * ファイル名を指定して {@link FileName} インスタンスを生成します。
     *
     * @param value ファイル名
     * @return {@link FileName} インスタンス
     * @throws IllegalArgumentException バリデーションエラーの場合
     */
    public static FileName of(final String value) {

        // null 禁止
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("File name cannot be null");
        }

        // 空文字のみを不許可
        if (value.isBlank()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }

        // 文字数上限チェック
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("File name exceeds the limit of " + MAX_LENGTH + " characters");
        }

        // 禁止文字（OS/URL危険文字）チェック
        if (value.matches(INVALID_CHARS_PATTERN)) {
            throw new IllegalArgumentException("File name contains invalid characters: / \\ : * ? \" < > |");
        }

        // パストラバーサル要素（..）の禁止
        if (value.contains("..")) {
            throw new IllegalArgumentException("File name cannot contain path traversal elements ('..')");
        }

        return new FileName(value);
    }

    public String value() {
        return value;
    }
}