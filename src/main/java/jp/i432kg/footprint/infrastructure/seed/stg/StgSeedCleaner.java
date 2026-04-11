package jp.i432kg.footprint.infrastructure.seed.stg;

import jp.i432kg.footprint.domain.value.ObjectKey;
import jp.i432kg.footprint.domain.value.StorageObject;
import jp.i432kg.footprint.infrastructure.storage.LocalStoragePathResolver;
import jp.i432kg.footprint.infrastructure.storage.S3ObjectResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * STG seed が作成した DB レコードと保存画像を削除するクリーンアップ処理です。
 * <p>
 * 保存先が S3 の場合とローカルストレージの場合の両方に対応します。
 * </p>
 */
@Slf4j
@Component
@Profile("stg")
@RequiredArgsConstructor
public class StgSeedCleaner {

    private final StgSeedAdminMapper seedAdminMapper;
    private final ObjectProvider<S3Client> s3ClientProvider;
    private final ObjectProvider<S3ObjectResolver> s3ObjectResolverProvider;
    private final ObjectProvider<LocalStoragePathResolver> localStoragePathResolverProvider;

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
            final S3Client s3Client = s3ClientProvider.getIfAvailable();
            final S3ObjectResolver s3ObjectResolver = s3ObjectResolverProvider.getIfAvailable();
            if (s3Client != null && s3ObjectResolver != null) {
                final StorageObject storageObject = StorageObject.s3(ObjectKey.of(objectKeyValue));
                s3Client.deleteObject(
                        DeleteObjectRequest.builder()
                                .bucket(s3ObjectResolver.resolveBucket(storageObject))
                                .key(s3ObjectResolver.resolveKey(storageObject))
                                .build()
                );
                return;
            }

            final LocalStoragePathResolver localResolver = localStoragePathResolverProvider.getIfAvailable();
            if (localResolver != null) {
                final StorageObject storageObject = StorageObject.local(ObjectKey.of(objectKeyValue));
                final Path path = localResolver.resolve(storageObject);
                Files.deleteIfExists(path);
            }
        } catch (IOException | RuntimeException e) {
            log.warn("Failed to delete seed image object. objectKey={}", objectKeyValue, e);
        }
    }
}
