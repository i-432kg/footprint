package jp.i432kg.footprint.logging.masking;

import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.domain.value.HashedPassword;
import jp.i432kg.footprint.domain.value.RawPassword;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * ログおよび API エラーレスポンスに含まれる機微情報をマスクします。
 */
@Component
public class SensitiveDataMasker {

    /**
     * Map 形式の詳細情報を再帰的に走査し、機微情報をマスクします。
     *
     * @param source マスク対象の詳細情報
     * @return マスク後の詳細情報
     */
    public Map<String, Object> maskMap(final Map<String, Object> source) {
        final Map<String, Object> sanitized = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            sanitized.put(entry.getKey(), Objects.requireNonNull(maskValue(entry.getKey(), entry.getValue(), source)));
        }
        return Map.copyOf(sanitized);
    }

    /**
     * バリデーションエラーの rejectedValue をフィールド名に応じてマスクします。
     *
     * @param field フィールド名または target 名
     * @param rejectedValue 入力値
     * @return マスク後の値
     */
    public @Nullable Object maskRejectedValue(final String field, final @Nullable Object rejectedValue) {
        if (Objects.isNull(rejectedValue)) {
            return null;
        }

        return maskByType(rejectedValue)
                .or(() -> findTarget(field).map(target -> target.mask(rejectedValue)))
                .orElse(rejectedValue);
    }

    /**
     * 値の構造に応じて再帰的にマスク処理を適用します。
     */
    private @Nullable Object maskValue(
            final String key,
            final @Nullable Object value,
            final Map<String, Object> context
    ) {
        if (value instanceof Map<?, ?> nestedMap) {
            return maskNestedMap(nestedMap);
        }

        if (value instanceof List<?> list) {
            return list.stream()
                    .map(item -> {
                        if (item instanceof Map<?, ?> nestedMap) {
                            return maskNestedMap(nestedMap);
                        }
                        return maskSimpleValue(key, item);
                    })
                    .toList();
        }

        // rejectedValue は target 名を見てマスク対象を判定する。
        if ("rejectedValue".equals(key)) {
            final Object target = context.get("target");
            return maskRejectedValue(String.valueOf(target), value);
        }

        return maskSimpleValue(key, value);
    }

    /**
     * 単一値に対して型ベース判定とキー名ベース判定を適用します。
     */
    private @Nullable Object maskSimpleValue(final String key, final @Nullable Object value) {
        if (Objects.isNull(value)) {
            return null;
        }

        return maskByType(value)
                .or(() -> findTarget(key).map(target -> target.mask(value)))
                .orElse(value);
    }

    /**
     * 値オブジェクトの型から機微情報を判定し、必要ならマスクします。
     */
    private Optional<Object> maskByType(final @Nullable Object value) {
        if (Objects.isNull(value)) {
            return Optional.empty();
        }

        if (value instanceof RawPassword || value instanceof HashedPassword) {
            return Optional.of(MaskingStrategy.FULL.mask(value));
        }

        if (value instanceof EmailAddress emailAddress) {
            return Optional.of(MaskingStrategy.EMAIL.mask(emailAddress.getValue()));
        }

        return Optional.empty();
    }

    /**
     * キー名からマスキング対象を解決します。
     */
    private Optional<MaskingTarget> findTarget(final String key) {
        return Arrays.stream(MaskingTarget.values())
                .filter(target -> target.matches(key))
                .findFirst();
    }

    /**
     * 任意のキー型を持つ Map を、ログ出力向けの文字列キー Map として再構築してマスクします。
     */
    private Map<String, Object> maskNestedMap(final Map<?, ?> source) {
        final Map<String, Object> typedMap = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            typedMap.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return maskMap(typedMap);
    }
}
