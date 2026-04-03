package jp.i432kg.footprint.infrastructure.seed;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * S3 上の seed-images.json から seed 投稿に利用する画像オブジェクトキー一覧を読み込む。
 */
@Component
@Profile("stg")
@RequiredArgsConstructor
public class SeedImageManifestLoader {

    private final S3SeedSourceImageProvider seedSourceImageProvider;
    private final StgSeedProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * seed-images.json から画像オブジェクトキー一覧を読み込む。
     * <p>
     * 以下の形式を許容する。
     * </p>
     * <ul>
     *     <li>文字列配列</li>
     *     <li>{"images": [{"objectKey": "..."}]}</li>
     *     <li>オブジェクト配列（objectKey / object_key を保持）</li>
     * </ul>
     *
     * @return 重複を除去したオブジェクトキー一覧
     */
    public List<String> loadObjectKeys() {
        final String manifestObjectKey = properties.getManifestObjectKey();

        try (SeedSourceImage manifest = seedSourceImageProvider.load(manifestObjectKey);
             InputStream inputStream = manifest.inputStream()) {
            return extractObjectKeys(inputStream, manifestObjectKey);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read seed image manifest. objectKey=" + manifestObjectKey, e);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Failed to load seed image manifest. objectKey=" + manifestObjectKey, e);
        }
    }

    private List<String> extractObjectKeys(final InputStream inputStream, final String manifestObjectKey) throws IOException {
        final JsonNode rootNode = objectMapper.readTree(inputStream);
        final Set<String> objectKeys = new LinkedHashSet<>();

        if (rootNode.isArray()) {
            extractFromArrayNode(rootNode, objectKeys);
            return new ArrayList<>(objectKeys);
        }

        if (rootNode.isObject() && rootNode.has("images") && rootNode.get("images").isArray()) {
            extractFromArrayNode(rootNode.get("images"), objectKeys);
            return new ArrayList<>(objectKeys);
        }

        throw new IllegalStateException("Unsupported seed image manifest format. objectKey=" + manifestObjectKey);
    }

    private void extractFromArrayNode(final JsonNode arrayNode, final Set<String> objectKeys) {
        for (JsonNode node : arrayNode) {
            if (node.isTextual()) {
                addIfPresent(objectKeys, node.asText());
                continue;
            }

            if (node.isObject()) {
                addIfPresent(objectKeys, readObjectKey(node));
            }
        }
    }

    private String readObjectKey(final JsonNode node) {
        if (node.hasNonNull("objectKey")) {
            return node.get("objectKey").asText();
        }
        if (node.hasNonNull("object_key")) {
            return node.get("object_key").asText();
        }
        return null;
    }

    private void addIfPresent(final Set<String> objectKeys, final String objectKey) {
        if (Objects.nonNull(objectKey) && !objectKey.isBlank()) {
            objectKeys.add(objectKey);
        }
    }
}
