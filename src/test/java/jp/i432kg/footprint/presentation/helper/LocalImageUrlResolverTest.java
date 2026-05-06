package jp.i432kg.footprint.presentation.helper;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.value.StorageObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocalImageUrlResolverTest {

    @Test
    @DisplayName("LocalImageUrlResolver は LOCAL 保存画像の公開 URL を解決する")
    void should_resolveLocalImageUrl_when_storageObjectIsLocal() {
        final LocalImageUrlResolver resolver = new LocalImageUrlResolver();
        ReflectionTestUtils.setField(resolver, "imageBaseUrl", "/images/");

        final String actual = resolver.resolve(StorageObject.local(DomainTestFixtures.objectKey()));

        assertThat(actual).isEqualTo("/images/" + DomainTestFixtures.objectKey().getValue());
    }

    @Test
    @DisplayName("LocalImageUrlResolver は LOCAL 以外の保存種別を拒否する")
    void should_throwException_when_storageObjectIsNotLocal() {
        final LocalImageUrlResolver resolver = new LocalImageUrlResolver();
        ReflectionTestUtils.setField(resolver, "imageBaseUrl", "/images/");

        assertThatThrownBy(() -> resolver.resolve(StorageObject.s3(DomainTestFixtures.objectKey())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("storageObject is not LOCAL.");
    }

}
