package jp.i432kg.footprint.logging.access;

import jp.i432kg.footprint.logging.LoggingEvents;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 1 リクエスト分の access ログ補助情報を保持するコンテキストです。
 *
 * <p>controller はこのコンテキストへ event 名や追加項目を設定し、
 * 最終的なログ出力は `AccessLogFilter` が一括で行います。
 */
public final class AccessLogContext {

    // すべての request が @LogOperation 付き endpoint を通るとは限らないため nullable で保持する
    @Setter
    private @Nullable String operation;

    @Setter
    private String event = LoggingEvents.HTTP_ACCESS;
    private final Map<String, Object> fields = new LinkedHashMap<>();

    /**
     * access ログへ出力するイベント名を返します。
     *
     * @return access ログのイベント名
     */
    public String event() {
        return event;
    }

    /**
     * failure / warning 系 event 解決に利用する operation 名を返します。
     *
     * @return operation 名。未設定時は空
     */
    public Optional<String> operation() {
        return Optional.ofNullable(operation);
    }

    /**
     * access ログへ出力する追加項目を登録します。
     *
     * @param fieldName  追加するログ項目名
     * @param fieldValue 追加するログ項目値
     */
    public void addField(final String fieldName, final Object fieldValue) {
        fields.put(fieldName, fieldValue);
    }

    /**
     * access ログへ出力する追加項目を返します。
     *
     * @return 追加ログ項目の読み取り専用マップ
     */
    public Map<String, Object> fields() {
        return Collections.unmodifiableMap(fields);
    }
}
