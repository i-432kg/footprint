package jp.i432kg.footprint.presentation.web;

import jp.i432kg.footprint.config.FrontendAssetProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuiltFrontendAssetModelAdviceTest {

    @Mock
    private ViteManifestAssetResolver viteManifestAssetResolver;

    @Test
    @DisplayName("BuiltFrontendAssetModelAdvice は resolver が解決した frontend asset 情報を返す")
    void should_returnResolvedFrontendAssets_when_frontendAssetsIsCalled() {
        final FrontendAssetProperties properties = new FrontendAssetProperties();
        when(viteManifestAssetResolver.resolve()).thenReturn(properties);

        final FrontendAssetProperties actual = new BuiltFrontendAssetModelAdvice(viteManifestAssetResolver)
                .frontendAssets();

        assertThat(actual).isSameAs(properties);
        verify(viteManifestAssetResolver).resolve();
    }

}
