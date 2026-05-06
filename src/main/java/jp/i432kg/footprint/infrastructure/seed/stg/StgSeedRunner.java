package jp.i432kg.footprint.infrastructure.seed.stg;

import jp.i432kg.footprint.logging.LoggingEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * アプリケーション起動時に STG seed の実行可否を判定し、必要なら投入を開始する runner です。
 * <p>
 * {@code app.stg-seed.enabled=true} の場合のみ seed を実行する。
 * </p>
 */
@Slf4j
@Component
@Profile("stg")
@RequiredArgsConstructor
public class StgSeedRunner implements ApplicationRunner {

    private final StgSeedProperties properties;
    private final StgSeedService stgSeedService;
    private final StgSeedCleaner stgSeedCleaner;

    /**
     * 起動引数を受け取り、設定に応じて STG seed の cleanup と投入を実行します。
     *
     * @param args 起動引数
     */
    @Override
    public void run(final ApplicationArguments args) {
        if (!properties.isEnabled()) {
            log.atInfo()
                    .addKeyValue("event", LoggingEvents.STG_SEED_SKIPPED)
                    .addKeyValue("enabled", false)
                    .log("STG seed skipped");
            return;
        }

        validateSeedProperties();

        log.atInfo()
                .addKeyValue("event", LoggingEvents.STG_SEED_STARTED)
                .log("STG seed started");

        if (properties.isCleanupBeforeSeed() || properties.isCleanupOnly()) {
            log.atInfo()
                    .addKeyValue("event", LoggingEvents.STG_SEED_CLEANUP_STARTED)
                    .log("STG seed cleanup started");
            stgSeedCleaner.cleanup();
            log.atInfo()
                    .addKeyValue("event", LoggingEvents.STG_SEED_CLEANUP_FINISHED)
                    .log("STG seed cleanup finished");
        }

        if (properties.isCleanupOnly()) {
            log.atInfo()
                    .addKeyValue("event", LoggingEvents.STG_SEED_CLEANUP_ONLY_COMPLETED)
                    .log("STG seed cleanup only completed");
            return;
        }

        stgSeedService.seed();

        log.atInfo()
                .addKeyValue("event", LoggingEvents.STG_SEED_FINISHED)
                .log("STG seed finished");
    }

    private void validateSeedProperties() {
        require(properties.getTestPassword(), "APP_STG_SEED_TEST_PASSWORD");
        require(properties.getSourceBucketName(), "APP_STG_SEED_SOURCE_BUCKET_NAME");
    }

    private void require(final String value, final String envName) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Seed is enabled, but required environment variable is missing: " + envName
            );
        }
    }
}
