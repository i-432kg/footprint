package jp.i432kg.footprint.infrastructure.seed.local;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.local-seed")
public class LocalSeedProperties {

    private boolean enabled = false;
    private boolean cleanupOnly = false;
    private boolean cleanupBeforeSeed = false;
    private String testPassword;
    private String emailPrefix = "local_seed_user_";
    private String sourceRootDir = "seed-source";
    private String manifestPath = "seed-source/manifest/seed-images.json";
}
