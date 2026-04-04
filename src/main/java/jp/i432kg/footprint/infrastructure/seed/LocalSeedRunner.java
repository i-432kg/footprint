package jp.i432kg.footprint.infrastructure.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalSeedRunner implements ApplicationRunner {

    private final LocalSeedProperties properties;
    private final LocalSeedService localSeedService;
    private final LocalSeedCleaner localSeedCleaner;

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
