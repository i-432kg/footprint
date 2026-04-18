package jp.i432kg.footprint.presentation.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.i432kg.footprint.config.FrontendAssetProperties;
import jp.i432kg.footprint.config.ViteManifestProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Vite manifest から画面ごとの JS/CSS asset を解決する component です。
 * <p>
 * stg/prod 環境で manifest を読み込み、テンプレートから参照する
 * {@link FrontendAssetProperties} を組み立てます。
 */
@Component
@Profile({"stg", "prod"})
public class ViteManifestAssetResolver {

    private final ResourceLoader resourceLoader;
    private final ViteManifestProperties viteManifestProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ViteManifestAssetResolver(
            final ResourceLoader resourceLoader,
            final ViteManifestProperties viteManifestProperties
    ) {
        this.resourceLoader = resourceLoader;
        this.viteManifestProperties = viteManifestProperties;
    }

    /**
     * manifest を読み込み、各画面エントリに対応する frontend asset 情報を生成します。
     *
     * @return テンプレートから参照する frontend asset 情報
     * @throws IllegalStateException manifest が存在しない場合、読み込みに失敗した場合、
     *                               または必須エントリが存在しない場合
     */
    public FrontendAssetProperties resolve() {
        final Map<String, ManifestChunk> manifest = loadManifest();

        final FrontendAssetProperties properties = new FrontendAssetProperties();
        properties.getEntries().setLogin(toAsset(manifest, "src/entries/login/main.js"));
        properties.getEntries().setMap(toAsset(manifest, "src/entries/map/main.js"));
        properties.getEntries().setMypage(toAsset(manifest, "src/entries/mypage/main.js"));
        properties.getEntries().setSearch(toAsset(manifest, "src/entries/search/main.js"));
        properties.getEntries().setTimeline(toAsset(manifest, "src/entries/timeline/main.js"));
        return properties;
    }

    private Map<String, ManifestChunk> loadManifest() {
        final String location = viteManifestProperties.getManifestLocation();
        try {
            final Resource resource = resourceLoader.getResource(location);

            if (!resource.exists()) {
                throw new IllegalStateException("Vite manifest not found. location=" + location);
            }

            try (InputStream inputStream = resource.getInputStream()) {
                return objectMapper.readValue(
                        inputStream,
                        new TypeReference<Map<String, ManifestChunk>>() {}
                );
            }
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to load Vite manifest. location=" + location,
                    e
            );
        }
    }

    private FrontendAssetProperties.Asset toAsset(
            final Map<String, ManifestChunk> manifest,
            final String entryKey
    ) {
        final ManifestChunk entry = manifest.get(entryKey);
        if (entry == null) {
            throw new IllegalStateException("Vite manifest entry not found: " + entryKey);
        }

        final FrontendAssetProperties.Asset asset = new FrontendAssetProperties.Asset();
        asset.setJs("/" + entry.getFile());
        asset.setCss(collectCss(manifest, entry));
        return asset;
    }

    private List<String> collectCss(
            final Map<String, ManifestChunk> manifest,
            final ManifestChunk entry
    ) {
        final LinkedHashSet<String> cssFiles = new LinkedHashSet<>();
        final Set<String> visited = new HashSet<>();
        collectCssRecursive(manifest, entry, cssFiles, visited);
        return new ArrayList<>(cssFiles);
    }

    private void collectCssRecursive(
            final Map<String, ManifestChunk> manifest,
            final ManifestChunk chunk,
            final LinkedHashSet<String> cssFiles,
            final Set<String> visited
    ) {
        if (chunk == null || chunk.getFile() == null || !visited.add(chunk.getFile())) {
            return;
        }

        if (chunk.getCss() != null) {
            for (String css : chunk.getCss()) {
                cssFiles.add("/" + css);
            }
        }

        if (chunk.getImports() != null) {
            for (String importKey : chunk.getImports()) {
                collectCssRecursive(manifest, manifest.get(importKey), cssFiles, visited);
            }
        }
    }

    /**
     * Vite manifest の 1 エントリを表す DTO です。
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ManifestChunk {
        private String file;
        private List<String> css;
        private List<String> imports;

        public String getFile() {
            return file;
        }

        public void setFile(final String file) {
            this.file = file;
        }

        public List<String> getCss() {
            return css;
        }

        public void setCss(final List<String> css) {
            this.css = css;
        }

        public List<String> getImports() {
            return imports;
        }

        public void setImports(final List<String> imports) {
            this.imports = imports;
        }
    }
}
