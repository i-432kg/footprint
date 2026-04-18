package jp.i432kg.footprint.presentation.web;

import jp.i432kg.footprint.config.FrontendAssetProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LocalFrontendAssetModelAdviceTest {

    @Test
    @DisplayName("LocalFrontendAssetModelAdvice は設定済み frontend asset 情報をそのまま返す")
    void should_returnConfiguredFrontendAssets_when_frontendAssetsIsCalled() {
        final FrontendAssetProperties properties = new FrontendAssetProperties();
        properties.getEntries().getTimeline().setJs("http://localhost:5173/src/entries/timeline/main.js");

        final FrontendAssetProperties actual = new LocalFrontendAssetModelAdvice(properties).frontendAssets();

        assertThat(actual).isSameAs(properties);
    }

}
