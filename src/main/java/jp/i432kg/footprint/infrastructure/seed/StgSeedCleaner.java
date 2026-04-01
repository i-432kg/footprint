package jp.i432kg.footprint.infrastructure.seed;

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
 * STG seed で作成したデータを削除するクリーンアップ処理。
 * <p>
 * DB 上の seed データを削除するだけでなく、保存先に残った画像ファイルも削除する。
 * 保存先が S3 の場合とローカルストレージの場合の両方に対応する。
 * </p>
 */
@Slf4j
@Component
@Profile("stg")
@RequiredArgsConstructor
public class StgSeedCleaner {

    private final SeedAdminMapper seedAdminMapper;
    private final ObjectProvider<S3Client> s3ClientProvider;
    private final ObjectProvider<S3ObjectResolver> s3ObjectResolverProvider;
    private final ObjectProvider<LocalStoragePathResolver> localStoragePathResolverProvider;

    /**
     * seed で作成した投稿・返信・ユーザー・画像を削除する。
     */
    @Transactional
    public void cleanup() {
        // まず画像実体を削除してから、関連レコードを削除する
        final List<String> objectKeys = seedAdminMapper.findSeedImageObjectKeys();
        objectKeys.forEach(this::deletePhysicalObjectQuietly);

        seedAdminMapper.deleteSeedReplies();
        seedAdminMapper.deleteSeedPostImages();
        seedAdminMapper.deleteSeedPosts();
        seedAdminMapper.deleteSeedUsers();
    }

    /**
     * 画像オブジェクトを例外を握りつぶしながら削除する。
     *
     * @param objectKeyValue 削除対象オブジェクトキー
     */
    private void deletePhysicalObjectQuietly(final String objectKeyValue) {
        try {
            final S3Client s3Client = s3ClientProvider.getIfAvailable();
            final S3ObjectResolver s3ObjectResolver = s3ObjectResolverProvider.getIfAvailable();
            if (s3Client != null && s3ObjectResolver != null) {
                // S3 保存の場合はバケット名・キーを解決して削除する
                final StorageObject storageObject = StorageObject.s3(ObjectKey.of(objectKeyValue));
                s3Client.deleteObject(
                        DeleteObjectRequest.builder()
                                .bucket(s3ObjectResolver.resolveBucket(storageObject))
                                .key(s3ObjectResolver.resolveKey(storageObject))
                                .build()
                );
                return;
            }

            // ローカル保存の場合はファイルパスを解決して削除する
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