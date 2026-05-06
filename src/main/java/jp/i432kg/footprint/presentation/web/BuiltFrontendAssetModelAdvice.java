package jp.i432kg.footprint.presentation.web;

import jp.i432kg.footprint.config.FrontendAssetProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * stg/prod 環境向けに、Vite manifest から解決した frontend asset 情報をモデルへ公開する advice です。
 */
@ControllerAdvice
@Profile({"stg", "prod"})
public class BuiltFrontendAssetModelAdvice {

    private final ViteManifestAssetResolver viteManifestAssetResolver;

    public BuiltFrontendAssetModelAdvice(final ViteManifestAssetResolver viteManifestAssetResolver) {
        this.viteManifestAssetResolver = viteManifestAssetResolver;
    }

    /**
     * Vite manifest を基に解決した frontend asset 情報をモデル属性 `frontendAssets` として公開します。
     *
     * @return テンプレートから参照する frontend asset 情報
     */
    @ModelAttribute("frontendAssets")
    public FrontendAssetProperties frontendAssets() {
        return viteManifestAssetResolver.resolve();
    }
}
