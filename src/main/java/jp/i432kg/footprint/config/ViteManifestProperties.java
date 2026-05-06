package jp.i432kg.footprint.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.frontend.vite")
public class ViteManifestProperties {

    /**
     * Dockerfile で frontend/dist を src/main/resources/static にコピーする前提。
     * そのため Vite の manifest は classpath:/static/manifest.json から解決する。
     */
    private String manifestLocation = "classpath:/static/manifest.json";
}
