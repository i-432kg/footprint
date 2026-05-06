package jp.i432kg.footprint.domain.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jp.i432kg.footprint.domain.value.ValueObjectTestSupport.assertInvalidValue;
import static org.assertj.core.api.Assertions.assertThat;

class StorageObjectTest {

    @Test
    @DisplayName("StorageObject.of は妥当な storageType と objectKey の組み合わせを生成する")
    void should_createStorageObject_when_argumentsAreValid() {
        final ObjectKey objectKey = ObjectKey.of("a/b.jpg");

        final StorageObject actual = StorageObject.of(StorageType.LOCAL, objectKey);

        assertThat(actual.getStorageType()).isEqualTo(StorageType.LOCAL);
        assertThat(actual.getObjectKey()).isEqualTo(objectKey);
    }

    @Test
    @DisplayName("StorageObject.of は storageType が null の場合を拒否する")
    void should_throwException_when_storageTypeIsNull() {
        assertInvalidValue(
                () -> StorageObject.of(null, ObjectKey.of("a/b.jpg")),
                "storage_type",
                "required"
        );
    }

    @Test
    @DisplayName("StorageObject.of は objectKey が null の場合を拒否する")
    void should_throwException_when_objectKeyIsNull() {
        assertInvalidValue(
                () -> StorageObject.of(StorageType.LOCAL, null),
                "object_key",
                "required"
        );
    }

    @Test
    @DisplayName("StorageObject.local は LOCAL の保存先として生成する")
    void should_createLocalStorageObject_when_localFactoryIsUsed() {
        final StorageObject actual = StorageObject.local(ObjectKey.of("a/b.jpg"));

        assertThat(actual.getStorageType()).isEqualTo(StorageType.LOCAL);
        assertThat(actual.isLocal()).isTrue();
        assertThat(actual.isS3()).isFalse();
    }

    @Test
    @DisplayName("StorageObject.s3 は S3 の保存先として生成する")
    void should_createS3StorageObject_when_s3FactoryIsUsed() {
        final StorageObject actual = StorageObject.s3(ObjectKey.of("a/b.jpg"));

        assertThat(actual.getStorageType()).isEqualTo(StorageType.S3);
        assertThat(actual.isS3()).isTrue();
        assertThat(actual.isLocal()).isFalse();
    }

    @Test
    @DisplayName("StorageObject の判定メソッドは保存先種別と一致する")
    void should_returnFlagsMatchingStorageType_when_predicatesAreCalled() {
        final StorageObject local = StorageObject.local(ObjectKey.of("a/b.jpg"));
        final StorageObject s3 = StorageObject.s3(ObjectKey.of("a/b.jpg"));

        assertThat(local.isLocal()).isTrue();
        assertThat(local.isS3()).isFalse();
        assertThat(s3.isLocal()).isFalse();
        assertThat(s3.isS3()).isTrue();
    }
}
