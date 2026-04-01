package jp.i432kg.footprint.infrastructure.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("stg")
@RequiredArgsConstructor
public class StgSeedRunner implements ApplicationRunner {

    private final StgSeedProperties properties;
    private final StgSeedService stgSeedService;
    private final StgSeedCleaner stgSeedCleaner;

    @Override
    public void run(final ApplicationArguments args) {
        if (!properties.isEnabled()) {
            log.info("STG seed skipped. app.seed.enabled=false");
            return;
        }

        log.info("STG seed start.");

        if (properties.isCleanupBeforeSeed()) {
            log.info("STG seed cleanup start.");
            stgSeedCleaner.cleanup();
            log.info("STG seed cleanup finished.");
        }

        stgSeedService.seed();

        log.info("STG seed finished.");
    }
}
