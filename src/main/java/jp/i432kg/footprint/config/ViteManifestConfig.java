package jp.i432kg.footprint.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ViteManifestProperties.class)
public class ViteManifestConfig {
}