package jp.i432kg.footprint.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FrontendAssetProperties.class)
public class FrontendAssetConfig {
}