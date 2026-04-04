package jp.i432kg.footprint.presentation.web;

import jp.i432kg.footprint.config.FrontendAssetProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

public class FrontendAssetModelAdvice {

    @ControllerAdvice
    @Profile("local")
    public static class LocalFrontendAssetModelAdvice {

        private final FrontendAssetProperties frontendAssetProperties;

        public LocalFrontendAssetModelAdvice(final FrontendAssetProperties frontendAssetProperties) {
            this.frontendAssetProperties = frontendAssetProperties;
        }

        @ModelAttribute("frontendAssets")
        public FrontendAssetProperties frontendAssets() {
            return frontendAssetProperties;
        }
    }

    @ControllerAdvice
    @Profile({"stg", "prod"})
    public static class BuiltFrontendAssetModelAdvice {

        private final ViteManifestAssetResolver viteManifestAssetResolver;

        public BuiltFrontendAssetModelAdvice(final ViteManifestAssetResolver viteManifestAssetResolver) {
            this.viteManifestAssetResolver = viteManifestAssetResolver;
        }

        @ModelAttribute("frontendAssets")
        public FrontendAssetProperties frontendAssets() {
            return viteManifestAssetResolver.resolve();
        }
    }
}