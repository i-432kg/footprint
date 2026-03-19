package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Objects;

/**
 * 投稿を一意に識別するための ID を表す値オブジェクト
 * <p>
 * フロント側に公開する ID として扱う
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostId {

    String value;

    /**
     * 数値を指定して {@link PostId} インスタンスを生成します。
     *
     * @param value 投稿 ID
     * @return {@link PostId} インスタンス
     * @throws InvalidValueException バリデーションエラーの場合
     */
    public static PostId of(String value) {
        if (Objects.isNull(value)) {
            throw new InvalidValueException("common.invalid.null", "field.post_id");
        }
        if (value.isBlank()) {
            throw new InvalidValueException("common.invalid.blank", "field.post_id");
        }
        return new PostId(value);
    }

    public String value() {
        return value;
    }
}