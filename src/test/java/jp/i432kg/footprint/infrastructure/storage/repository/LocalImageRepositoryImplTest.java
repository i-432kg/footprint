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
import jp.i432kg.footprint.infrastructure.storage.LocalStoragePathResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalImageRepositoryImplTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-04-19T00:00:00Z"),
            ZoneId.of("Asia/Tokyo")
    );

    @TempDir
    Path tempDir;

    @Mock
    private LocalStoragePathResolver localStoragePathResolver;

    @Mock
    private ImageIdGenerator imageIdGenerator;

    @Test
    @DisplayName("LocalImageRepositoryImpl.store は JPEG 画像をローカル保存して StorageObject を返す")
    void should_storeJpegImageToLocalPath_when_validImageProvided() throws IOException {
        final byte[] imageBytes = StorageRepositoryTestSupport.jpegBytes();
        final FileName originalFilename = FileName.of("sample.png");
        final UserId userId = DomainTestFixtures.userId();
        final PostId postId = DomainTestFixtures.postId();
        final ImageId imageId = DomainTestFixtures.imageId();
        final ObjectKey expectedObjectKey =
                ObjectKey.createPostImageKey(userId, postId, imageId, FileExtension.of("jpg"));
        final StorageObject expectedStorageObject = StorageObject.local(expectedObjectKey);
        final Path finalPath = tempDir.resolve(expectedObjectKey.getValue());

        when(imageIdGenerator.generate()).thenReturn(imageId);
        when(localStoragePathResolver.resolve(expectedStorageObject)).thenReturn(finalPath);

        final StorageObject actual = newRepository().store(
                new ByteArrayInputStream(imageBytes),
                originalFilename,
                userId,
                postId
        );

        assertThat(actual).isEqualTo(expectedStorageObject);
        assertThat(Files.readAllBytes(finalPath)).isEqualTo(imageBytes);
        verify(imageIdGenerator).generate();
        verify(localStoragePathResolver).resolve(expectedStorageObject);
        verifyNoMoreInteractions(imageIdGenerator, localStoragePathResolver);
    }

    @Test
    @DisplayName("LocalImageRepositoryImpl.store は画像形式判定不能時に元ファイル名の拡張子へフォールバックする")
    void should_fallbackToOriginalExtension_when_fileTypeCannotBeDetected() throws IOException {
        final byte[] imageBytes = "not-an-image".getBytes();
        final FileName originalFilename = FileName.of("sample.webp");
        final UserId userId = DomainTestFixtures.userId();
        final PostId postId = DomainTestFixtures.postId();
        final ImageId imageId = DomainTestFixtures.imageId();
        final ObjectKey expectedObjectKey =
                ObjectKey.createPostImageKey(userId, postId, imageId, FileExtension.of("webp"));
        final StorageObject expectedStorageObject = StorageObject.local(expectedObjectKey);
        final Path finalPath = tempDir.resolve(expectedObjectKey.getValue());

        when(imageIdGenerator.generate()).thenReturn(imageId);
        when(localStoragePathResolver.resolve(expectedStorageObject)).thenReturn(finalPath);

        try (MockedStatic<FileTypeDetector> fileTypeDetector = mockStatic(FileTypeDetector.class)) {
            fileTypeDetector.when(() -> FileTypeDetector.detectFileType(any()))
                    .thenReturn(FileType.Unknown);

            final StorageObject actual = newRepository().store(
                    new ByteArrayInputStream(imageBytes),
                    originalFilename,
                    userId,
                    postId
            );

            assertThat(actual).isEqualTo(expectedStorageObject);
            assertThat(Files.readAllBytes(finalPath)).isEqualTo(imageBytes);
        }

        verify(imageIdGenerator).generate();
        verify(localStoragePathResolver).resolve(expectedStorageObject);
        verifyNoMoreInteractions(imageIdGenerator, localStoragePathResolver);
    }

    @Test
    @DisplayName("LocalImageRepositoryImpl.extract は EXIF 付きローカル画像からメタデータを抽出する")
    void should_extractMetadataFromLocalImage_when_exifImageProvided() throws Exception {
        final BigDecimal latitude = new BigDecimal("35.681236");
        final BigDecimal longitude = new BigDecimal("139.767125");
        final LocalDateTime takenAt = LocalDateTime.of(2026, 4, 1, 12, 30);
        final byte[] imageBytes = StorageRepositoryTestSupport.jpegBytes();
        final Path imagePath = tempDir.resolve("sample.jpg");
        Files.write(imagePath, imageBytes);
        final StorageObject storageObject = StorageObject.local(DomainTestFixtures.objectKey());
        final Metadata metadata = StorageRepositoryTestSupport.metadataWithExifAndGps(latitude, longitude, takenAt);

        when(localStoragePathResolver.resolve(storageObject)).thenReturn(imagePath);

        try (MockedStatic<ImageMetadataReader> imageMetadataReader = mockStatic(ImageMetadataReader.class)) {
            imageMetadataReader.when(() -> ImageMetadataReader.readMetadata(imagePath.toFile()))
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
            assertThat(actual.getFileSize().getValue()).isEqualTo(imageBytes.length);
        }

        verify(localStoragePathResolver).resolve(storageObject);
        verifyNoMoreInteractions(localStoragePathResolver, imageIdGenerator);
    }

    @Test
    @DisplayName("LocalImageRepositoryImpl.extract は GPS と撮影日時が無い場合に既定値を使用する")
    void should_useFallbackValues_when_gpsAndTakenAtAreMissing() throws Exception {
        final byte[] imageBytes = StorageRepositoryTestSupport.pngBytes(4, 5);
        final Path imagePath = tempDir.resolve("sample.png");
        Files.write(imagePath, imageBytes);
        final StorageObject storageObject = StorageObject.local(DomainTestFixtures.objectKey());
        final Metadata metadata = StorageRepositoryTestSupport.metadataWithoutExif();

        when(localStoragePathResolver.resolve(storageObject)).thenReturn(imagePath);

        try (MockedStatic<ImageMetadataReader> imageMetadataReader = mockStatic(ImageMetadataReader.class)) {
            imageMetadataReader.when(() -> ImageMetadataReader.readMetadata(imagePath.toFile()))
                    .thenReturn(metadata);

            final ImageMetadata actual = newRepository().extract(storageObject);

            assertThat(actual.getWidth().getValue()).isEqualTo(4);
            assertThat(actual.getHeight().getValue()).isEqualTo(5);
            assertThat(actual.getLocation()).isEqualTo(Location.unknown());
            assertThat(actual.isHasEXIF()).isFalse();
            assertThat(actual.getTakenAt()).isEqualTo(LocalDateTime.now(FIXED_CLOCK));
            assertThat(actual.getFileExtension()).isEqualTo(FileExtension.of("png"));
            assertThat(actual.getFileSize().getValue()).isEqualTo(imageBytes.length);
        }

        verify(localStoragePathResolver).resolve(storageObject);
        verifyNoMoreInteractions(localStoragePathResolver, imageIdGenerator);
    }

    @Test
    @DisplayName("LocalImageRepositoryImpl.delete は対象ローカルファイルを削除する")
    void should_deleteLocalFile_when_storageObjectExists() throws IOException {
        final StorageObject storageObject = StorageObject.local(DomainTestFixtures.objectKey());
        final Path imagePath = tempDir.resolve("delete-target.jpg");
        Files.write(imagePath, "delete-me".getBytes());
        when(localStoragePathResolver.resolve(storageObject)).thenReturn(imagePath);

        newRepository().delete(storageObject);

        assertThat(Files.exists(imagePath)).isFalse();
        verify(localStoragePathResolver).resolve(storageObject);
        verifyNoMoreInteractions(localStoragePathResolver, imageIdGenerator);
    }

    @Test
    @DisplayName("LocalImageRepositoryImpl.store は非対応画像形式の場合に IOException を送出する")
    void should_throwIOException_when_imageFormatIsUnsupported() {
        final FileName originalFilename = FileName.of("sample.txt");

        try (MockedStatic<FileTypeDetector> fileTypeDetector = mockStatic(FileTypeDetector.class)) {
            fileTypeDetector.when(() -> FileTypeDetector.detectFileType(any()))
                    .thenReturn(FileType.Unknown);

            assertThatThrownBy(() -> newRepository().store(
                    new ByteArrayInputStream("not-an-image".getBytes()),
                    originalFilename,
                    DomainTestFixtures.userId(),
                    DomainTestFixtures.postId()
            ))
                    .isInstanceOf(IOException.class)
                    .hasMessage("サポートされていない画像形式です。")
                    .hasCauseInstanceOf(InvalidValueException.class);
        }

        verifyNoMoreInteractions(localStoragePathResolver, imageIdGenerator);
    }

    @Test
    @DisplayName("LocalImageRepositoryImpl.store はファイル移動失敗時に IOException を再送出する")
    void should_rethrowIOException_when_moveFails() throws IOException {
        final byte[] imageBytes = StorageRepositoryTestSupport.jpegBytes();
        final FileName originalFilename = FileName.of("sample.jpg");
        final UserId userId = DomainTestFixtures.userId();
        final PostId postId = DomainTestFixtures.postId();
        final ImageId imageId = DomainTestFixtures.imageId();
        final ObjectKey expectedObjectKey =
                ObjectKey.createPostImageKey(userId, postId, imageId, FileExtension.of("jpg"));
        final StorageObject expectedStorageObject = StorageObject.local(expectedObjectKey);
        final Path occupied = tempDir.resolve("occupied");
        Files.write(occupied, "not-a-directory".getBytes());
        final Path invalidFinalPath = occupied.resolve("sample.jpg");

        when(imageIdGenerator.generate()).thenReturn(imageId);
        when(localStoragePathResolver.resolve(expectedStorageObject)).thenReturn(invalidFinalPath);

        assertThatThrownBy(() -> newRepository().store(
                new ByteArrayInputStream(imageBytes),
                originalFilename,
                userId,
                postId
        ))
                .isInstanceOf(IOException.class);

        verify(imageIdGenerator).generate();
        verify(localStoragePathResolver).resolve(expectedStorageObject);
        verifyNoMoreInteractions(imageIdGenerator, localStoragePathResolver);
    }

    @Test
    @DisplayName("LocalImageRepositoryImpl.extract はメタデータ解析失敗時に ImageProcessingException を送出する")
    void should_throwImageProcessingException_when_metadataExtractionFails() throws Exception {
        final byte[] imageBytes = StorageRepositoryTestSupport.pngBytes(2, 2);
        final Path imagePath = tempDir.resolve("sample.txt");
        Files.write(imagePath, imageBytes);
        final StorageObject storageObject = StorageObject.local(DomainTestFixtures.objectKey());

        when(localStoragePathResolver.resolve(storageObject)).thenReturn(imagePath);

        assertThatThrownBy(() -> newRepository().extract(storageObject))
                .isInstanceOf(ImageProcessingException.class)
                .hasMessage("画像メタデータの解析に失敗しました: " + storageObject.getObjectKey().getValue());

        verify(localStoragePathResolver).resolve(storageObject);
        verifyNoMoreInteractions(localStoragePathResolver, imageIdGenerator);
    }

    private LocalImageRepositoryImpl newRepository() {
        return new LocalImageRepositoryImpl(
                tempDir.toString(),
                localStoragePathResolver,
                FIXED_CLOCK,
                imageIdGenerator
        );
    }
}
