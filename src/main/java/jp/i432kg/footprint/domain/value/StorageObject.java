package jp.i432kg.footprint.domain.value;

import jp.i432kg.footprint.domain.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * ファイルの保存場所を表す値オブジェクト
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StorageObject {

    static String FIELD_NAME_STORAGE_TYPE = "storage_type";
    static String FIELD_NAME_OBJECT_KEY = "object_key";

    StorageType storageType;
    ObjectKey objectKey;

    public static StorageObject of(final @Nullable StorageType storageType, final @Nullable ObjectKey objectKey) {

        // null 禁止
        if (Objects.isNull(storageType)) {
            throw InvalidValueException.required(FIELD_NAME_STORAGE_TYPE);
        }

        // null 禁止
        if (Objects.isNull(objectKey)) {
            throw InvalidValueException.required(FIELD_NAME_OBJECT_KEY);
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
