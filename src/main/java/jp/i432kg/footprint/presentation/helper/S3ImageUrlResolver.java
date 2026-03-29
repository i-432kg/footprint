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

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.storage.type", havingValue = "S3")
public class S3ImageUrlResolver implements ImageUrlResolver {

    private final S3ObjectResolver s3ObjectResolver;
    private final S3StorageProperties s3StorageProperties;
    private final S3Presigner s3Presigner;

    @Override
    public String resolve(final StorageObject storageObject) {
        if (storageObject == null || storageObject.getObjectKey() == null) {
            return null;
        }

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