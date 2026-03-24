package jp.i432kg.footprint.presentation.web;

import jp.i432kg.footprint.config.FrontendAssetProperties;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class FrontendAssetModelAdvice {

    private final FrontendAssetProperties frontendAssetProperties;

    public FrontendAssetModelAdvice(final FrontendAssetProperties frontendAssetProperties) {
        this.frontendAssetProperties = frontendAssetProperties;
    }

    @ModelAttribute("frontendAssets")
    public FrontendAssetProperties frontendAssets() {
        return frontendAssetProperties;
    }
}