package jp.i432kg.footprint.infrastructure.seed.stg;

import jp.i432kg.footprint.domain.value.ObjectKey;
import jp.i432kg.footprint.domain.value.StorageObject;
import jp.i432kg.footprint.logging.LoggingEvents;
import jp.i432kg.footprint.infrastructure.storage.S3ObjectResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.util.List;

/**
 * STG seed が作成した DB レコードと S3 上の保存画像を削除するクリーンアップ処理です。
 */
@Slf4j
@Component
@Profile("stg")
@RequiredArgsConstructor
public class StgSeedCleaner {

    private final StgSeedAdminMapper seedAdminMapper;
    private final S3Client s3Client;
    private final S3ObjectResolver s3ObjectResolver;

    /**
     * STG seed が作成した画像実体と関連レコードを削除します。
     */
    @Transactional
    public void cleanup() {
        final List<String> objectKeys = seedAdminMapper.findSeedImageObjectKeys();
        objectKeys.forEach(this::deletePhysicalObjectQuietly);

        seedAdminMapper.deleteSeedReplies();
        seedAdminMapper.deleteSeedPostImages();
        seedAdminMapper.deleteSeedPosts();
        seedAdminMapper.deleteSeedUsers();
    }

    private void deletePhysicalObjectQuietly(final String objectKeyValue) {
        try {
            final StorageObject storageObject = StorageObject.s3(ObjectKey.of(objectKeyValue));
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(s3ObjectResolver.resolveBucket(storageObject))
                            .key(s3ObjectResolver.resolveKey(storageObject))
                            .build()
            );
        } catch (RuntimeException e) {
            log.atWarn()
                    .addKeyValue("event", LoggingEvents.STG_SEED_IMAGE_DELETE_FAILED)
                    .addKeyValue("objectKey", objectKeyValue)
                    .setCause(e)
                    .log("Failed to delete STG seed image");
        }
    }
}
