package jp.i432kg.footprint.infrastructure.seed.local;

import jp.i432kg.footprint.domain.value.ObjectKey;
import jp.i432kg.footprint.domain.value.StorageObject;
import jp.i432kg.footprint.logging.LoggingEvents;
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

/**
 * local seed が作成した DB レコードとローカル保存画像を削除するクリーンアップ処理です。
 */
@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalSeedCleaner {

    private final LocalSeedAdminMapper localSeedAdminMapper;
    private final LocalStoragePathResolver localStoragePathResolver;

    /**
     * local seed が作成した画像実体と関連レコードを削除します。
     */
    @Transactional
    public void cleanup() {
        final List<String> objectKeys = localSeedAdminMapper.findSeedImageObjectKeys();
        objectKeys.forEach(this::deletePhysicalObjectQuietly);

        deleteSeedRepliesFromLeaves();
        localSeedAdminMapper.deleteSeedPostImages();
        localSeedAdminMapper.deleteSeedPosts();
        localSeedAdminMapper.deleteSeedUsers();
    }

    private void deleteSeedRepliesFromLeaves() {
        final int deletedReplies = localSeedAdminMapper.deleteSeedLeafReplies();
        if (deletedReplies > 0) {
            deleteSeedRepliesFromLeaves();
        }
    }

    private void deletePhysicalObjectQuietly(final String objectKeyValue) {
        try {
            final StorageObject storageObject = StorageObject.local(ObjectKey.of(objectKeyValue));
            final Path path = localStoragePathResolver.resolve(storageObject);
            Files.deleteIfExists(path);
        } catch (IOException | RuntimeException e) {
            log.atWarn()
                    .addKeyValue("event", LoggingEvents.LOCAL_SEED_IMAGE_DELETE_FAILED)
                    .addKeyValue("objectKey", objectKeyValue)
                    .setCause(e)
                    .log("Failed to delete local seed image");
        }
    }
}
