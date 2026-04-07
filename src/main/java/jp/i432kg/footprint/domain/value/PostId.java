package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.helper.UlidValidation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.jspecify.annotations.Nullable;

/**
 * 投稿を一意に識別するための ID を表す値オブジェクト
 * <p>
 * フロント側に公開する ID として扱う
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostId {

    static String FIELD_NAME = "post_id";

    String value;

    /**
     * 数値を指定して {@link PostId} インスタンスを生成します。
     *
     * @param value 投稿 ID
     * @return {@link PostId} インスタンス
     * @throws jp.i432kg.footprint.domain.exception.InvalidValueException バリデーションエラーの場合
     */
    public static PostId of(final @Nullable String value) {
        final String validated = UlidValidation.requireValidUlid(FIELD_NAME, value);
        return new PostId(validated);
    }

    public String value() {
        return value;
    }
}
