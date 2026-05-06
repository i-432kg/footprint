package jp.i432kg.footprint.infrastructure.storage;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.value.StorageObject;
import jp.i432kg.footprint.infrastructure.config.S3StorageProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class S3ObjectResolverTest {

    @Test
    @DisplayName("S3ObjectResolver.resolveBucket は設定済み bucket 名を返す")
    void should_returnBucketName_when_storageObjectIsS3() {
        final S3StorageProperties properties = new S3StorageProperties();
        properties.setBucketName("footprint-bucket");
        final S3ObjectResolver resolver = new S3ObjectResolver(properties);

        final String actual = resolver.resolveBucket(StorageObject.s3(DomainTestFixtures.objectKey()));

        assertThat(actual).isEqualTo("footprint-bucket");
    }

    @Test
    @DisplayName("S3ObjectResolver.resolveKey は object key をそのまま返す")
    void should_returnObjectKeyValue_when_storageObjectIsS3() {
        final S3StorageProperties properties = new S3StorageProperties();
        properties.setBucketName("footprint-bucket");
        final S3ObjectResolver resolver = new S3ObjectResolver(properties);
        final StorageObject storageObject = StorageObject.s3(DomainTestFixtures.objectKey());

        final String actual = resolver.resolveKey(storageObject);

        assertThat(actual).isEqualTo(storageObject.getObjectKey().getValue());
    }

    @Test
    @DisplayName("S3ObjectResolver は S3 でない StorageObject を拒否する")
    void should_throwException_when_storageObjectIsNotS3() {
        final S3StorageProperties properties = new S3StorageProperties();
        properties.setBucketName("footprint-bucket");
        final S3ObjectResolver resolver = new S3ObjectResolver(properties);

        assertThatThrownBy(() -> resolver.resolveBucket(StorageObject.local(DomainTestFixtures.objectKey())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("storageObject is not S3.");

        assertThatThrownBy(() -> resolver.resolveKey(StorageObject.local(DomainTestFixtures.objectKey())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("storageObject is not S3.");
    }
}
