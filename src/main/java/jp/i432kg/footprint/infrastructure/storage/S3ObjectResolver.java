package jp.i432kg.footprint.infrastructure.storage;

import jp.i432kg.footprint.domain.value.StorageObject;

public class S3ObjectResolver {

    private final String bucketName;

    public S3ObjectResolver(String bucketName) {
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