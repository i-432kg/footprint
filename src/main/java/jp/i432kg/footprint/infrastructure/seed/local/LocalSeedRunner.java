package jp.i432kg.footprint.infrastructure.seed.local;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * アプリケーション起動時に local seed の実行可否を判定し、必要なら投入を開始する runner です。
 */
@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalSeedRunner implements ApplicationRunner {

    private final LocalSeedProperties properties;
    private final LocalSeedService localSeedService;
    private final LocalSeedCleaner localSeedCleaner;

    /**
     * 起動引数を受け取り、設定に応じて local seed の cleanup と投入を実行します。
     *
     * @param args 起動引数
     */
    @Override
    public void run(final ApplicationArguments args) {
        if (!properties.isEnabled()) {
            log.info("Local seed skipped. app.local-seed.enabled=false");
            return;
        }

        validateSeedProperties();

        log.info("Local seed start.");

        if (properties.isCleanupBeforeSeed() || properties.isCleanupOnly()) {
            log.info("Local seed cleanup start.");
            localSeedCleaner.cleanup();
            log.info("Local seed cleanup finished.");
        }

        if (properties.isCleanupOnly()) {
            log.info("Local seed cleanup only completed.");
            return;
        }

        localSeedService.seed();
        log.info("Local seed finished.");
    }

    private void validateSeedProperties() {
        require(properties.getTestPassword(), "app.local-seed.test-password");
    }

    private void require(final String value, final String keyName) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Local seed is enabled, but required property is missing: " + keyName);
        }
    }
}
