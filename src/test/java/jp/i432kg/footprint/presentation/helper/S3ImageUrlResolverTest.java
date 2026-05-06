package jp.i432kg.footprint.presentation.helper;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.value.StorageObject;
import jp.i432kg.footprint.infrastructure.config.S3StorageProperties;
import jp.i432kg.footprint.infrastructure.storage.S3ObjectResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ImageUrlResolverTest {

    @Mock
    private S3ObjectResolver s3ObjectResolver;

    @Mock
    private S3StorageProperties s3StorageProperties;

    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private PresignedGetObjectRequest presignedGetObjectRequest;

    @Test
    @DisplayName("S3ImageUrlResolver は S3 保存画像の presigned URL を解決する")
    void should_resolvePresignedUrl_when_storageObjectIsS3() throws MalformedURLException {
        final StorageObject storageObject = StorageObject.s3(DomainTestFixtures.objectKey());
        when(s3ObjectResolver.resolveBucket(storageObject)).thenReturn("footprint-bucket");
        when(s3ObjectResolver.resolveKey(storageObject)).thenReturn(storageObject.getObjectKey().getValue());
        when(s3StorageProperties.getPresignedGetExpireMinutes()).thenReturn(15L);
        when(s3Presigner.presignGetObject(org.mockito.ArgumentMatchers.any(GetObjectPresignRequest.class)))
                .thenReturn(presignedGetObjectRequest);
        when(presignedGetObjectRequest.url()).thenReturn(URI.create("https://example.com/presigned").toURL());

        final String actual = newResolver().resolve(storageObject);

        assertThat(actual).isEqualTo("https://example.com/presigned");
    }

    @Test
    @DisplayName("S3ImageUrlResolver は解決した bucket key と有効期限で presign request を組み立てる")
    void should_buildPresignRequestWithResolvedBucketKeyAndExpireMinutes_when_resolvingUrl() throws MalformedURLException {
        final StorageObject storageObject = StorageObject.s3(DomainTestFixtures.objectKey());
        when(s3ObjectResolver.resolveBucket(storageObject)).thenReturn("footprint-bucket");
        when(s3ObjectResolver.resolveKey(storageObject)).thenReturn(storageObject.getObjectKey().getValue());
        when(s3StorageProperties.getPresignedGetExpireMinutes()).thenReturn(15L);
        when(s3Presigner.presignGetObject(org.mockito.ArgumentMatchers.any(GetObjectPresignRequest.class)))
                .thenReturn(presignedGetObjectRequest);
        when(presignedGetObjectRequest.url()).thenReturn(URI.create("https://example.com/presigned").toURL());

        newResolver().resolve(storageObject);

        final ArgumentCaptor<GetObjectPresignRequest> captor = ArgumentCaptor.forClass(GetObjectPresignRequest.class);
        verify(s3Presigner).presignGetObject(captor.capture());
        final GetObjectPresignRequest request = captor.getValue();

        assertThat(request.signatureDuration()).isEqualTo(Duration.ofMinutes(15));
        assertThat(request.getObjectRequest().bucket()).isEqualTo("footprint-bucket");
        assertThat(request.getObjectRequest().key()).isEqualTo(storageObject.getObjectKey().getValue());
    }

    @Test
    @DisplayName("S3ImageUrlResolver は S3 以外の保存種別を拒否する")
    void should_throwException_when_storageObjectIsNotS3() {
        assertThatThrownBy(() -> newResolver().resolve(StorageObject.local(DomainTestFixtures.objectKey())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("storageObject is not S3.");
    }

    private S3ImageUrlResolver newResolver() {
        return new S3ImageUrlResolver(s3ObjectResolver, s3StorageProperties, s3Presigner);
    }

}
