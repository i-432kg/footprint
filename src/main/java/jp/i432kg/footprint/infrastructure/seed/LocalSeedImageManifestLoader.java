package jp.i432kg.footprint.infrastructure.seed;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalSeedImageManifestLoader {

    private final LocalSeedProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<String> loadImagePaths() {
        final Path manifestPath = Paths.get(properties.getManifestPath()).normalize();
        if (!Files.exists(manifestPath)) {
            log.warn("Local seed manifest not found. path={}", manifestPath);
            return List.of();
        }

        try (InputStream inputStream = Files.newInputStream(manifestPath)) {
            final List<String> entries = extractEntries(inputStream, manifestPath.toString());
            final Path sourceRoot = Paths.get(properties.getSourceRootDir()).normalize();
            return entries.stream()
                    .map(entry -> resolvePath(sourceRoot, entry))
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read local seed image manifest. path=" + manifestPath, e);
        }
    }

    private String resolvePath(final Path sourceRoot, final String entry) {
        final Path entryPath = Paths.get(entry);
        if (entryPath.isAbsolute()) {
            return entryPath.normalize().toString();
        }
        if (entry.startsWith(sourceRoot.toString())) {
            return entryPath.normalize().toString();
        }
        return sourceRoot.resolve(entry).normalize().toString();
    }

    private List<String> extractEntries(final InputStream inputStream, final String manifestPath) throws IOException {
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

        throw new IllegalStateException("Unsupported local seed image manifest format. path=" + manifestPath);
    }

    private void extractFromArrayNode(final JsonNode arrayNode, final Set<String> entries) {
        for (JsonNode node : arrayNode) {
            if (node.isTextual()) {
                addIfPresent(entries, node.asText());
                continue;
            }
            if (node.isObject()) {
                addIfPresent(entries, readPath(node));
            }
        }
    }

    private String readPath(final JsonNode node) {
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
