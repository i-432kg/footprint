package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.DomainException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * ファイルの保存場所を表す値オブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StorageObject {

    StorageType storageType;
    ObjectKey objectKey;

    public static StorageObject of(final StorageType storageType, final ObjectKey objectKey) {

        if (storageType == null) {
            throw new DomainException("storageType must not be null.");
        }
        if (objectKey == null) {
            throw new DomainException("objectKey must not be null.");
        }

        return new StorageObject(storageType, objectKey);
    }

    public static StorageObject local(ObjectKey objectKey) {
        return new StorageObject(StorageType.LOCAL, objectKey);
    }

    public static StorageObject s3(ObjectKey objectKey) {
        return new StorageObject(StorageType.S3, objectKey);
    }

    public boolean isLocal() {
        return storageType == StorageType.LOCAL;
    }

    public boolean isS3() {
        return storageType == StorageType.S3;
    }
}
