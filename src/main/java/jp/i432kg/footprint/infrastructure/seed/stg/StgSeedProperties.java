package jp.i432kg.footprint.infrastructure.seed.stg;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * STG seed 実行に関する設定値。
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.stg-seed")
public class StgSeedProperties {

    private boolean enabled = false;
    private boolean cleanupOnly = false;
    private boolean cleanupBeforeSeed = false;
    private String testPassword;
    private int activeUserCount = 2;
    private int inactiveUserCount = 1;
    private String emailPrefix = "stg_seed_user_";
    private String sourceBucketName;
    private String manifestObjectKey = "seed-source/manifest/seed-images.json";
}
