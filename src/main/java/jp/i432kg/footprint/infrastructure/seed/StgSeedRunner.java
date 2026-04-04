package jp.i432kg.footprint.infrastructure.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * アプリケーション起動時に STG seed の実行可否を判定する Runner。
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
     * 起動時に seed の実行処理を行う。
     *
     * @param args 起動引数
     */
    @Override
    public void run(final ApplicationArguments args) {
        // seed 無効なら何もせず終了する
        if (!properties.isEnabled()) {
            log.info("STG seed skipped. app.stg-seed.enabled=false");
            return;
        }

        validateSeedProperties();

        log.info("STG seed start.");

        // 既存の seed データを削除してから投入したい場合のみ cleanup を実行する
        if (properties.isCleanupBeforeSeed() || properties.isCleanupOnly()) {
            log.info("STG seed cleanup start.");
            stgSeedCleaner.cleanup();
            log.info("STG seed cleanup finished.");
        }

        if (properties.isCleanupOnly()) {
            log.info("STG seed cleanup only completed.");
            return;
        }

        stgSeedService.seed();

        log.info("STG seed finished.");
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