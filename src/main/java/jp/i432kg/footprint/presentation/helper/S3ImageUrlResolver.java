package jp.i432kg.footprint.presentation.helper;

import jp.i432kg.footprint.domain.value.StorageObject;
import jp.i432kg.footprint.infrastructure.config.S3StorageProperties;
import jp.i432kg.footprint.infrastructure.storage.S3ObjectResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

/**
 * S3 保存画像に対する presigned URL を解決する resolver です。
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.storage.type", havingValue = "S3")
public class S3ImageUrlResolver implements ImageUrlResolver {

    private final S3ObjectResolver s3ObjectResolver;
    private final S3StorageProperties s3StorageProperties;
    private final S3Presigner s3Presigner;

    /**
     * S3 保存画像の一時取得 URL を解決します。
     *
     * @param storageObject S3 保存の {@link StorageObject}
     * @return 画像取得用の presigned URL
     * @throws NullPointerException storageObject が {@code null} の場合
     * @throws IllegalArgumentException LOCAL など S3 以外の保存種別が渡された場合
     */
    @Override
    public String resolve(final StorageObject storageObject) {

        if (!storageObject.isS3()) {
            throw new IllegalArgumentException("storageObject is not S3.");
        }

        final String bucket = s3ObjectResolver.resolveBucket(storageObject);
        final String key = s3ObjectResolver.resolveKey(storageObject);

        final GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        final GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(s3StorageProperties.getPresignedGetExpireMinutes()))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}
