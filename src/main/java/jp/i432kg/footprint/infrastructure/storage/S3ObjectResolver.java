package jp.i432kg.footprint.infrastructure.storage;

import jp.i432kg.footprint.domain.value.StorageObject;
import jp.i432kg.footprint.infrastructure.config.S3StorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.storage.type", havingValue = "S3")
public class S3ObjectResolver {

    private final S3StorageProperties properties;

    public String resolveBucket(final StorageObject storageObject) {
        if (!storageObject.isS3()) {
            throw new IllegalArgumentException("storageObject is not S3.");
        }
        return properties.getBucketName();
    }

    public String resolveKey(final StorageObject storageObject) {
        if (!storageObject.isS3()) {
            throw new IllegalArgumentException("storageObject is not S3.");
        }
        return storageObject.getObjectKey().getValue();
    }
}