package jp.i432kg.footprint.infrastructure.seed.shared;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * seed image manifest を解析してエントリ一覧を返す共通パーサです。
 */
@Component
public class SeedImageManifestParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * seed image manifest を解析し、重複除去済みのエントリ一覧を返します。
     *
     * @param inputStream manifest 入力ストリーム
     * @param sourceName エラー表示用の入力元識別子
     * @return manifest エントリ一覧
     * @throws IOException manifest 読み込みに失敗した場合
     */
    public List<String> parseEntries(final InputStream inputStream, final String sourceName) throws IOException {
        final JsonNode rootNode = objectMapper.readTree(inputStream);
        final Set<String> entries = new LinkedHashSet<>();

        if (rootNode.isArray()) {
            extractFromArrayNode(rootNode, entries);
            return new ArrayList<>(entries);
        }

        if (rootNode.isObject() && rootNode.has("images") && rootNode.get("images").isArray()) {
            extractFromArrayNode(rootNode.get("images"), entries);
            return new ArrayList<>(entries);
        }

        throw new IllegalStateException("Unsupported seed image manifest format. source=" + sourceName);
    }

    private void extractFromArrayNode(final JsonNode arrayNode, final Set<String> entries) {
        for (JsonNode node : arrayNode) {
            if (node.isTextual()) {
                addIfPresent(entries, node.asText());
                continue;
            }
            if (node.isObject()) {
                addIfPresent(entries, readEntry(node));
            }
        }
    }

    private String readEntry(final JsonNode node) {
        if (node.hasNonNull("path")) {
            return node.get("path").asText();
        }
        if (node.hasNonNull("objectKey")) {
            return node.get("objectKey").asText();
        }
        if (node.hasNonNull("object_key")) {
            return node.get("object_key").asText();
        }
        return null;
    }

    private void addIfPresent(final Set<String> entries, final String value) {
        if (Objects.nonNull(value) && !value.isBlank()) {
            entries.add(value);
        }
    }
}
