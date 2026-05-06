package jp.i432kg.footprint.presentation.web;

import jp.i432kg.footprint.config.FrontendAssetProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * local 環境向けに、設定済みの frontend asset 情報をモデルへ公開する advice です。
 */
@ControllerAdvice
@Profile("local")
public class LocalFrontendAssetModelAdvice {

    private final FrontendAssetProperties frontendAssetProperties;

    public LocalFrontendAssetModelAdvice(final FrontendAssetProperties frontendAssetProperties) {
        this.frontendAssetProperties = frontendAssetProperties;
    }

    /**
     * local 環境用の frontend asset 情報をモデル属性 `frontendAssets` として公開します。
     *
     * @return テンプレートから参照する frontend asset 情報
     */
    @ModelAttribute("frontendAssets")
    public FrontendAssetProperties frontendAssets() {
        return frontendAssetProperties;
    }
}
