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
     * そのため本番では classpath:/static/.vite/manifest.json を読む。
     */
    private String manifestLocation = "classpath:/static/manifest.json";
}