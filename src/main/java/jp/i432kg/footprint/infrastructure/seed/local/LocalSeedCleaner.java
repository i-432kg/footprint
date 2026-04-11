package jp.i432kg.footprint.infrastructure.seed.local;

import jp.i432kg.footprint.domain.value.ObjectKey;
import jp.i432kg.footprint.domain.value.StorageObject;
import jp.i432kg.footprint.infrastructure.storage.LocalStoragePathResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalSeedCleaner {

    private final LocalSeedAdminMapper localSeedAdminMapper;
    private final LocalStoragePathResolver localStoragePathResolver;

    @Transactional
    public void cleanup() {
        final List<String> objectKeys = localSeedAdminMapper.findSeedImageObjectKeys();
        objectKeys.forEach(this::deletePhysicalObjectQuietly);

        localSeedAdminMapper.deleteSeedReplies();
        localSeedAdminMapper.deleteSeedPostImages();
        localSeedAdminMapper.deleteSeedPosts();
        localSeedAdminMapper.deleteSeedUsers();
    }

    private void deletePhysicalObjectQuietly(final String objectKeyValue) {
        try {
            final StorageObject storageObject = StorageObject.local(ObjectKey.of(objectKeyValue));
            final Path path = localStoragePathResolver.resolve(storageObject);
            Files.deleteIfExists(path);
        } catch (IOException | RuntimeException e) {
            log.warn("Failed to delete local seed image object. objectKey={}", objectKeyValue, e);
        }
    }
}
