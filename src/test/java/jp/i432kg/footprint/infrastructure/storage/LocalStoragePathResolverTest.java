package jp.i432kg.footprint.infrastructure.storage;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.value.ObjectKey;
import jp.i432kg.footprint.domain.value.StorageObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocalStoragePathResolverTest {

    @Test
    @DisplayName("LocalStoragePathResolver.resolve はローカルストレージの保存先パスを解決する")
    void should_resolveLocalStoragePath_when_storageObjectIsLocal() {
        final LocalStoragePathResolver resolver = new LocalStoragePathResolver("storage/local");

        final Path actual = resolver.resolve(StorageObject.local(DomainTestFixtures.objectKey()));

        assertThat(actual)
                .isEqualTo(Paths.get("storage/local")
                        .resolve(DomainTestFixtures.objectKey().getValue())
                        .normalize());
    }

    @Test
    @DisplayName("LocalStoragePathResolver.resolve は相対セグメントを含む base dir を正規化して返す")
    void should_returnNormalizedPath_when_baseDirContainsRelativeSegments() {
        final LocalStoragePathResolver resolver = new LocalStoragePathResolver("storage/./images/../local");
        final ObjectKey objectKey = ObjectKey.of("users/sample/posts/sample/images/image.jpg");

        final Path actual = resolver.resolve(StorageObject.local(objectKey));

        assertThat(actual)
                .isEqualTo(Paths.get("storage/./images/../local")
                        .resolve(objectKey.getValue())
                        .normalize());
    }

    @Test
    @DisplayName("LocalStoragePathResolver.resolve は LOCAL でない StorageObject を拒否する")
    void should_throwException_when_storageObjectIsNotLocal() {
        final LocalStoragePathResolver resolver = new LocalStoragePathResolver("storage/local");

        assertThatThrownBy(() -> resolver.resolve(StorageObject.s3(DomainTestFixtures.objectKey())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("storageObject is not LOCAL.");
    }
}
