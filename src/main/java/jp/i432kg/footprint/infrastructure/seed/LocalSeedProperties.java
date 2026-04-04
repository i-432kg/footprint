package jp.i432kg.footprint.infrastructure.seed;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.local-seed")
public class LocalSeedProperties {

    private boolean enabled = true;
    private boolean cleanupOnly = false;
    private boolean cleanupBeforeSeed = true;
    private String testPassword = "localpass123";
    private int activeUserCount = 1;
    private int inactiveUserCount = 1;
    private String emailPrefix = "local_seed_user_";
    private String sourceRootDir = "seed-source";
    private String manifestPath = "seed-source/manifest/seed-images.json";
}
