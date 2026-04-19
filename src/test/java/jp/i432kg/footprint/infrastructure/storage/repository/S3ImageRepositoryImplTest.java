package jp.i432kg.footprint.infrastructure.storage.repository;

import com.drew.imaging.FileType;
import com.drew.imaging.FileTypeDetector;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import jp.i432kg.footprint.application.command.model.ImageMetadata;
import jp.i432kg.footprint.application.port.id.ImageIdGenerator;
import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.exception.InvalidValueException;
import jp.i432kg.footprint.domain.model.Location;
import jp.i432kg.footprint.domain.value.FileExtension;
import jp.i432kg.footprint.domain.value.FileName;
import jp.i432kg.footprint.domain.value.ImageId;
import jp.i432kg.footprint.domain.value.ObjectKey;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.StorageObject;
import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.infrastructure.storage.S3ObjectResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ImageRepositoryImplTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-04-19T00:00:00Z"),
            ZoneId.of("Asia/Tokyo")
    );

    @Mock
    private S3Client s3Client;

    @Mock
    private S3ObjectResolver s3ObjectResolver;

    @Mock
    private ImageIdGenerator imageIdGenerator;

    @Test
    @DisplayName("S3ImageRepositoryImpl.store は JPEG 画像を S3 にアップロードして StorageObject を返す")
    void should_uploadJpegImageToS3_when_validImageProvided() throws IOException {
        final byte[] imageBytes = StorageRepositoryTestSupport.jpegBytes(2, 3);
        final UserId userId = DomainTestFixtures.userId();
        final PostId postId = DomainTestFixtures.postId();
        final ImageId imageId = DomainTestFixtures.imageId();
        final ObjectKey expectedObjectKey =
                ObjectKey.createPostImageKey(userId, postId, imageId, FileExtension.of("jpg"));
        final StorageObject expectedStorageObject = StorageObject.s3(expectedObjectKey);

        when(imageIdGenerator.generate()).thenReturn(imageId);
        when(s3ObjectResolver.resolveBucket(expectedStorageObject)).thenReturn("footprint-bucket");
        when(s3ObjectResolver.resolveKey(expectedStorageObject)).thenReturn(expectedObjectKey.getValue());

        final StorageObject actual = newRepository().store(
                new ByteArrayInputStream(imageBytes),
                FileName.of("sample.png"),
                userId,
                postId
        );

        assertThat(actual).isEqualTo(expectedStorageObject);

        final ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));
        final PutObjectRequest request = requestCaptor.getValue();
        assertThat(request.bucket()).isEqualTo("footprint-bucket");
        assertThat(request.key()).isEqualTo(expectedObjectKey.getValue());
        assertThat(request.contentType()).isEqualTo("image/jpeg");
        assertThat(request.contentLength()).isEqualTo((long) imageBytes.length);

        verify(imageIdGenerator).generate();
        verify(s3ObjectResolver).resolveBucket(expectedStorageObject);
        verify(s3ObjectResolver).resolveKey(expectedStorageObject);
        verifyNoMoreInteractions(s3Client, s3ObjectResolver, imageIdGenerator);
    }

    @Test
    @DisplayName("S3ImageRepositoryImpl.store は画像形式判定不能時に元ファイル名の拡張子へフォールバックする")
    void should_fallbackToOriginalExtension_when_fileTypeCannotBeDetected() throws IOException {
        final byte[] imageBytes = "not-an-image".getBytes();
        final UserId userId = DomainTestFixtures.userId();
        final PostId postId = DomainTestFixtures.postId();
        final ImageId imageId = DomainTestFixtures.imageId();
        final ObjectKey expectedObjectKey =
                ObjectKey.createPostImageKey(userId, postId, imageId, FileExtension.of("webp"));
        final StorageObject expectedStorageObject = StorageObject.s3(expectedObjectKey);

        when(imageIdGenerator.generate()).thenReturn(imageId);
        when(s3ObjectResolver.resolveBucket(expectedStorageObject)).thenReturn("footprint-bucket");
        when(s3ObjectResolver.resolveKey(expectedStorageObject)).thenReturn(expectedObjectKey.getValue());

        try (MockedStatic<FileTypeDetector> fileTypeDetector = mockStatic(FileTypeDetector.class)) {
            fileTypeDetector.when(() -> FileTypeDetector.detectFileType(any()))
                    .thenReturn(FileType.Unknown);

            final StorageObject actual = newRepository().store(
                    new ByteArrayInputStream(imageBytes),
                    FileName.of("sample.webp"),
                    userId,
                    postId
            );

            assertThat(actual).isEqualTo(expectedStorageObject);
        }

        final ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));
        assertThat(requestCaptor.getValue().contentType()).isEqualTo("image/webp");
        verify(imageIdGenerator).generate();
        verify(s3ObjectResolver).resolveBucket(expectedStorageObject);
        verify(s3ObjectResolver).resolveKey(expectedStorageObject);
        verifyNoMoreInteractions(s3Client, s3ObjectResolver, imageIdGenerator);
    }

    @Test
    @DisplayName("S3ImageRepositoryImpl.extract は EXIF 付き S3 画像からメタデータを抽出する")
    void should_extractMetadataFromS3Object_when_exifImageProvided() throws Exception {
        final byte[] imageBytes = StorageRepositoryTestSupport.jpegBytes(2, 3);
        final BigDecimal latitude = new BigDecimal("35.681236");
        final BigDecimal longitude = new BigDecimal("139.767125");
        final LocalDateTime takenAt = LocalDateTime.of(2026, 4, 1, 12, 30);
        final Metadata metadata = StorageRepositoryTestSupport.metadataWithExifAndGps(latitude, longitude, takenAt);
        final StorageObject storageObject = StorageObject.s3(DomainTestFixtures.objectKey());
        final ResponseInputStream<GetObjectResponse> responseInputStream =
                StorageRepositoryTestSupport.responseInputStream(imageBytes);

        when(s3ObjectResolver.resolveBucket(storageObject)).thenReturn("footprint-bucket");
        when(s3ObjectResolver.resolveKey(storageObject)).thenReturn(storageObject.getObjectKey().getValue());
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder().contentLength(123L).build());
        when(s3Client.getObject(any(software.amazon.awssdk.services.s3.model.GetObjectRequest.class)))
                .thenReturn(responseInputStream);

        try (MockedStatic<ImageMetadataReader> imageMetadataReader = mockStatic(ImageMetadataReader.class)) {
            imageMetadataReader.when(() -> ImageMetadataReader.readMetadata(any(ByteArrayInputStream.class)))
                    .thenReturn(metadata);

            final ImageMetadata actual = newRepository().extract(storageObject);

            assertThat(actual.getWidth().getValue()).isEqualTo(2);
            assertThat(actual.getHeight().getValue()).isEqualTo(3);
            assertThat(actual.getLocation()).isEqualTo(Location.of(
                    jp.i432kg.footprint.domain.value.Latitude.of(latitude),
                    jp.i432kg.footprint.domain.value.Longitude.of(longitude)
            ));
            assertThat(actual.isHasEXIF()).isTrue();
            assertThat(actual.getTakenAt()).isEqualTo(takenAt);
            assertThat(actual.getFileExtension()).isEqualTo(FileExtension.of("jpg"));
            assertThat(actual.getFileSize().getValue()).isEqualTo(123L);
        }

        verify(s3ObjectResolver).resolveBucket(storageObject);
        verify(s3ObjectResolver).resolveKey(storageObject);
        verify(s3Client).headObject(any(HeadObjectRequest.class));
        verify(s3Client).getObject(any(software.amazon.awssdk.services.s3.model.GetObjectRequest.class));
        verifyNoMoreInteractions(s3Client, s3ObjectResolver, imageIdGenerator);
    }

    @Test
    @DisplayName("S3ImageRepositoryImpl.extract は GPS と撮影日時が無い場合に既定値を使用する")
    void should_useFallbackValues_when_gpsAndTakenAtAreMissing() throws Exception {
        final byte[] imageBytes = StorageRepositoryTestSupport.pngBytes(4, 5);
        final Metadata metadata = StorageRepositoryTestSupport.metadataWithoutExif();
        final StorageObject storageObject = StorageObject.s3(DomainTestFixtures.objectKey());
        final ResponseInputStream<GetObjectResponse> responseInputStream =
                StorageRepositoryTestSupport.responseInputStream(imageBytes);

        when(s3ObjectResolver.resolveBucket(storageObject)).thenReturn("footprint-bucket");
        when(s3ObjectResolver.resolveKey(storageObject)).thenReturn(storageObject.getObjectKey().getValue());
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder().build());
        when(s3Client.getObject(any(software.amazon.awssdk.services.s3.model.GetObjectRequest.class)))
                .thenReturn(responseInputStream);

        try (MockedStatic<ImageMetadataReader> imageMetadataReader = mockStatic(ImageMetadataReader.class)) {
            imageMetadataReader.when(() -> ImageMetadataReader.readMetadata(any(ByteArrayInputStream.class)))
                    .thenReturn(metadata);

            final ImageMetadata actual = newRepository().extract(storageObject);

            assertThat(actual.getWidth().getValue()).isEqualTo(4);
            assertThat(actual.getHeight().getValue()).isEqualTo(5);
            assertThat(actual.getLocation()).isEqualTo(Location.unknown());
            assertThat(actual.isHasEXIF()).isFalse();
            assertThat(actual.getTakenAt()).isEqualTo(LocalDateTime.now(FIXED_CLOCK));
            assertThat(actual.getFileExtension()).isEqualTo(FileExtension.of("jpg"));
            assertThat(actual.getFileSize().getValue()).isEqualTo(imageBytes.length);
        }

        verify(s3ObjectResolver).resolveBucket(storageObject);
        verify(s3ObjectResolver).resolveKey(storageObject);
        verify(s3Client).headObject(any(HeadObjectRequest.class));
        verify(s3Client).getObject(any(software.amazon.awssdk.services.s3.model.GetObjectRequest.class));
        verifyNoMoreInteractions(s3Client, s3ObjectResolver, imageIdGenerator);
    }

    @Test
    @DisplayName("S3ImageRepositoryImpl.delete は対象 S3 オブジェクトを削除する")
    void should_deleteS3Object_when_storageObjectProvided() throws IOException {
        final StorageObject storageObject = StorageObject.s3(DomainTestFixtures.objectKey());

        when(s3ObjectResolver.resolveBucket(storageObject)).thenReturn("footprint-bucket");
        when(s3ObjectResolver.resolveKey(storageObject)).thenReturn(storageObject.getObjectKey().getValue());

        newRepository().delete(storageObject);

        final ArgumentCaptor<DeleteObjectRequest> requestCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client).deleteObject(requestCaptor.capture());
        assertThat(requestCaptor.getValue().bucket()).isEqualTo("footprint-bucket");
        assertThat(requestCaptor.getValue().key()).isEqualTo(storageObject.getObjectKey().getValue());
        verify(s3ObjectResolver).resolveBucket(storageObject);
        verify(s3ObjectResolver).resolveKey(storageObject);
        verifyNoMoreInteractions(s3Client, s3ObjectResolver, imageIdGenerator);
    }

    @Test
    @DisplayName("S3ImageRepositoryImpl.store は非対応画像形式の場合に IOException を送出する")
    void should_throwIOException_when_imageFormatIsUnsupported() {
        try (MockedStatic<FileTypeDetector> fileTypeDetector = mockStatic(FileTypeDetector.class)) {
            fileTypeDetector.when(() -> FileTypeDetector.detectFileType(any()))
                    .thenReturn(FileType.Unknown);

            assertThatThrownBy(() -> newRepository().store(
                    new ByteArrayInputStream("not-an-image".getBytes()),
                    FileName.of("sample.txt"),
                    DomainTestFixtures.userId(),
                    DomainTestFixtures.postId()
            ))
                    .isInstanceOf(IOException.class)
                    .hasMessage("サポートされていない画像形式です。")
                    .hasCauseInstanceOf(InvalidValueException.class);
        }

        verifyNoMoreInteractions(s3Client, s3ObjectResolver, imageIdGenerator);
    }

    @Test
    @DisplayName("S3ImageRepositoryImpl.store は S3 アップロード失敗時に IOException を送出する")
    void should_throwIOException_when_uploadFails() {
        final byte[] imageBytes = "upload-test".getBytes();
        final UserId userId = DomainTestFixtures.userId();
        final PostId postId = DomainTestFixtures.postId();
        final ImageId imageId = DomainTestFixtures.imageId();
        final ObjectKey expectedObjectKey =
                ObjectKey.createPostImageKey(userId, postId, imageId, FileExtension.of("webp"));
        final StorageObject expectedStorageObject = StorageObject.s3(expectedObjectKey);

        when(imageIdGenerator.generate()).thenReturn(imageId);
        when(s3ObjectResolver.resolveBucket(expectedStorageObject)).thenReturn("footprint-bucket");
        when(s3ObjectResolver.resolveKey(expectedStorageObject)).thenReturn(expectedObjectKey.getValue());
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder().message("upload failed").build());

        try (MockedStatic<FileTypeDetector> fileTypeDetector = mockStatic(FileTypeDetector.class)) {
            fileTypeDetector.when(() -> FileTypeDetector.detectFileType(any()))
                    .thenReturn(FileType.Unknown);

            assertThatThrownBy(() -> newRepository().store(
                    new ByteArrayInputStream(imageBytes),
                    FileName.of("sample.webp"),
                    userId,
                    postId
            ))
                    .isInstanceOf(IOException.class)
                    .hasMessage("S3への画像アップロードに失敗しました。")
                    .hasCauseInstanceOf(S3Exception.class);
        }

        verify(imageIdGenerator).generate();
        verify(s3ObjectResolver).resolveBucket(expectedStorageObject);
        verify(s3ObjectResolver).resolveKey(expectedStorageObject);
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verifyNoMoreInteractions(s3Client, s3ObjectResolver, imageIdGenerator);
    }

    @Test
    @DisplayName("S3ImageRepositoryImpl.extract は S3 オブジェクト未存在時に IOException を送出する")
    void should_throwIOException_when_s3ObjectDoesNotExist() {
        final StorageObject storageObject = StorageObject.s3(DomainTestFixtures.objectKey());

        when(s3ObjectResolver.resolveBucket(storageObject)).thenReturn("footprint-bucket");
        when(s3ObjectResolver.resolveKey(storageObject)).thenReturn(storageObject.getObjectKey().getValue());
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().message("not found").build());

        assertThatThrownBy(() -> newRepository().extract(storageObject))
                .isInstanceOf(IOException.class)
                .hasMessage("S3上の画像ファイルが見つかりません: " + storageObject.getObjectKey().getValue())
                .hasCauseInstanceOf(NoSuchKeyException.class);

        verify(s3ObjectResolver).resolveBucket(storageObject);
        verify(s3ObjectResolver).resolveKey(storageObject);
        verify(s3Client).headObject(any(HeadObjectRequest.class));
        verifyNoMoreInteractions(s3Client, s3ObjectResolver, imageIdGenerator);
    }

    @Test
    @DisplayName("S3ImageRepositoryImpl.extract と delete は S3 アクセス失敗時に IOException を送出する")
    void should_throwIOException_when_s3AccessFails() {
        final StorageObject storageObject = StorageObject.s3(DomainTestFixtures.objectKey());
        when(s3ObjectResolver.resolveBucket(storageObject)).thenReturn("footprint-bucket");
        when(s3ObjectResolver.resolveKey(storageObject)).thenReturn(storageObject.getObjectKey().getValue());
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(S3Exception.builder().message("head failed").build());
        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .thenThrow(S3Exception.builder().message("delete failed").build());

        assertThatThrownBy(() -> newRepository().extract(storageObject))
                .isInstanceOf(IOException.class)
                .hasMessage("S3上の画像ファイルへのアクセスに失敗しました: " + storageObject.getObjectKey().getValue())
                .hasCauseInstanceOf(S3Exception.class);

        assertThatThrownBy(() -> newRepository().delete(storageObject))
                .isInstanceOf(IOException.class)
                .hasMessage("S3上の画像削除に失敗しました。")
                .hasCauseInstanceOf(S3Exception.class);

        verify(s3ObjectResolver, times(2)).resolveBucket(storageObject);
        verify(s3ObjectResolver, times(2)).resolveKey(storageObject);
        verify(s3Client).headObject(any(HeadObjectRequest.class));
        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
        verifyNoMoreInteractions(s3Client, s3ObjectResolver, imageIdGenerator);
    }

    @Test
    @DisplayName("S3ImageRepositoryImpl.extract はオブジェクトキーから拡張子を取得できない場合に ImageProcessingException を送出する")
    void should_throwImageProcessingException_when_extensionCannotBeExtractedFromKey() throws Exception {
        final byte[] imageBytes = StorageRepositoryTestSupport.pngBytes(2, 2);
        final StorageObject storageObject = StorageObject.s3(DomainTestFixtures.objectKey());
        final ResponseInputStream<GetObjectResponse> responseInputStream =
                StorageRepositoryTestSupport.responseInputStream(imageBytes);

        when(s3ObjectResolver.resolveBucket(storageObject)).thenReturn("footprint-bucket");
        when(s3ObjectResolver.resolveKey(storageObject)).thenReturn("users/sample/posts/sample/images/no-extension");
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenReturn(HeadObjectResponse.builder().contentLength((long) imageBytes.length).build());
        when(s3Client.getObject(any(software.amazon.awssdk.services.s3.model.GetObjectRequest.class)))
                .thenReturn(responseInputStream);

        assertThatThrownBy(() -> newRepository().extract(storageObject))
                .isInstanceOf(ImageProcessingException.class)
                .hasMessage("画像メタデータの解析に失敗しました: " + storageObject.getObjectKey().getValue());

        verify(s3ObjectResolver).resolveBucket(storageObject);
        verify(s3ObjectResolver).resolveKey(storageObject);
        verify(s3Client).headObject(any(HeadObjectRequest.class));
        verify(s3Client).getObject(any(software.amazon.awssdk.services.s3.model.GetObjectRequest.class));
        verifyNoMoreInteractions(s3Client, s3ObjectResolver, imageIdGenerator);
    }

    private S3ImageRepositoryImpl newRepository() {
        return new S3ImageRepositoryImpl(s3Client, s3ObjectResolver, FIXED_CLOCK, imageIdGenerator);
    }
}
