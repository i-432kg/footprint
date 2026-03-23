package jp.i432kg.footprint.infrastructure.storage;

import jp.i432kg.footprint.domain.value.StorageObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.storage.type", havingValue = "S3")
public class S3ObjectResolver {

    private final String bucketName;

    public S3ObjectResolver(@Value("${app.storage.s3.bucket-name}") String bucketName) {
        this.bucketName = bucketName;
    }

    public String resolveBucket(StorageObject storageObject) {
        if (!storageObject.isS3()) {
            throw new IllegalArgumentException("storageObject is not S3.");
        }
        return bucketName;
    }

    public String resolveKey(StorageObject storageObject) {
        if (!storageObject.isS3()) {
            throw new IllegalArgumentException("storageObject is not S3.");
        }
        return storageObject.getObjectKey().getValue();
    }
}