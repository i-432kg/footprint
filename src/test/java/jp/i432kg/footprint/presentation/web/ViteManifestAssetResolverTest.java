package jp.i432kg.footprint.presentation.web;

import jp.i432kg.footprint.config.FrontendAssetProperties;
import jp.i432kg.footprint.config.ViteManifestProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ViteManifestAssetResolverTest {

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private Resource resource;

    @Test
    @DisplayName("ViteManifestAssetResolver は manifest から全画面の frontend asset 情報を解決する")
    void should_resolveFrontendAssets_when_manifestContainsAllRequiredEntries() {
        when(resourceLoader.getResource("classpath:/static/manifest.json"))
                .thenReturn(manifestResource(validManifestJson()));

        final FrontendAssetProperties actual = newResolver().resolve();

        assertThat(actual.getEntries().getLogin().getJs()).isEqualTo("/assets/login.js");
        assertThat(actual.getEntries().getMap().getJs()).isEqualTo("/assets/map.js");
        assertThat(actual.getEntries().getMypage().getJs()).isEqualTo("/assets/mypage.js");
        assertThat(actual.getEntries().getSearch().getJs()).isEqualTo("/assets/search.js");
        assertThat(actual.getEntries().getTimeline().getJs()).isEqualTo("/assets/timeline.js");
        assertThat(actual.getEntries().getTimeline().getCss())
                .containsExactly("/assets/timeline.css", "/assets/shared.css");
    }

    @Test
    @DisplayName("ViteManifestAssetResolver は import 先の CSS も再帰的に収集する")
    void should_collectImportedCssRecursively_when_entryHasImports() {
        when(resourceLoader.getResource("classpath:/static/manifest.json"))
                .thenReturn(manifestResource(validManifestJson()));

        final FrontendAssetProperties actual = newResolver().resolve();

        assertThat(actual.getEntries().getTimeline().getCss())
                .containsExactly("/assets/timeline.css", "/assets/shared.css");
    }

    @Test
    @DisplayName("ViteManifestAssetResolver は manifest が存在しない場合に例外を送出する")
    void should_throwException_when_manifestResourceDoesNotExist() {
        when(resourceLoader.getResource("classpath:/static/manifest.json")).thenReturn(resource);
        when(resource.exists()).thenReturn(false);

        assertThatThrownBy(() -> newResolver().resolve())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to load Vite manifest");
    }

    @Test
    @DisplayName("ViteManifestAssetResolver は manifest 読み込み失敗を例外にラップする")
    void should_throwException_when_manifestLoadingFails() throws IOException {
        when(resourceLoader.getResource("classpath:/static/manifest.json")).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenThrow(new IOException("broken"));

        assertThatThrownBy(() -> newResolver().resolve())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to load Vite manifest")
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    @DisplayName("ViteManifestAssetResolver は必須 manifest entry が欠落している場合に例外を送出する")
    void should_throwException_when_requiredManifestEntryIsMissing() {
        when(resourceLoader.getResource("classpath:/static/manifest.json"))
                .thenReturn(manifestResource(missingSearchEntryManifestJson()));

        assertThatThrownBy(() -> newResolver().resolve())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Vite manifest entry not found");
    }

    private ViteManifestAssetResolver newResolver() {
        final ViteManifestProperties properties = new ViteManifestProperties();
        properties.setManifestLocation("classpath:/static/manifest.json");
        return new ViteManifestAssetResolver(resourceLoader, properties);
    }

    private static Resource manifestResource(final String manifestJson) {
        return new ByteArrayResource(manifestJson.getBytes(StandardCharsets.UTF_8)) {
            @Override
            public boolean exists() {
                return true;
            }
        };
    }

    private static String validManifestJson() {
        return """
                {
                  "src/entries/login/main.js": {
                    "file": "assets/login.js",
                    "css": ["assets/login.css"]
                  },
                  "src/entries/map/main.js": {
                    "file": "assets/map.js",
                    "css": ["assets/map.css"]
                  },
                  "src/entries/mypage/main.js": {
                    "file": "assets/mypage.js",
                    "css": ["assets/mypage.css"]
                  },
                  "src/entries/search/main.js": {
                    "file": "assets/search.js",
                    "css": ["assets/search.css"]
                  },
                  "src/entries/timeline/main.js": {
                    "file": "assets/timeline.js",
                    "css": ["assets/timeline.css"],
                    "imports": ["src/shared/shared.js"]
                  },
                  "src/shared/shared.js": {
                    "file": "assets/shared.js",
                    "css": ["assets/shared.css"]
                  }
                }
                """;
    }

    private static String missingSearchEntryManifestJson() {
        return """
                {
                  "src/entries/login/main.js": {"file": "assets/login.js"},
                  "src/entries/map/main.js": {"file": "assets/map.js"},
                  "src/entries/mypage/main.js": {"file": "assets/mypage.js"},
                  "src/entries/timeline/main.js": {"file": "assets/timeline.js"}
                }
                """;
    }

}
