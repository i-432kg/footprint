package jp.i432kg.footprint.infrastructure.seed.local;

import jp.i432kg.footprint.infrastructure.seed.shared.SeedImageEntryLoader;
import jp.i432kg.footprint.infrastructure.seed.shared.SeedImageManifestParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * local 環境の seed image manifest を読み込み、投稿元画像のパス一覧へ解決するローダです。
 */
@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalSeedImageManifestLoader implements SeedImageEntryLoader {

    private final LocalSeedProperties properties;
    private final SeedImageManifestParser manifestParser;

    /**
     * local seed 用 manifest を読み込み、投稿元画像の絶対パス一覧を返します。
     *
     * @return 投稿元画像パス一覧。manifest が存在しない場合は空
     */
    public List<String> loadImagePaths() {
        final Path manifestPath = Paths.get(properties.getManifestPath()).normalize();
        if (!Files.exists(manifestPath)) {
            log.warn("Local seed manifest not found. path={}", manifestPath);
            return List.of();
        }

        try (InputStream inputStream = Files.newInputStream(manifestPath)) {
            final List<String> entries = manifestParser.parseEntries(inputStream, manifestPath.toString());
            final Path sourceRoot = Paths.get(properties.getSourceRootDir()).normalize();
            return entries.stream()
                    .map(entry -> resolvePath(sourceRoot, entry))
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read local seed image manifest. path=" + manifestPath, e);
        }
    }

    @Override
    public List<String> loadEntries() {
        return loadImagePaths();
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
}
