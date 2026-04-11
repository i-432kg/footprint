package jp.i432kg.footprint.infrastructure.storage.repository;

import com.drew.imaging.FileType;
import com.drew.imaging.FileTypeDetector;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.github.f4b6a3.ulid.UlidCreator;
import jp.i432kg.footprint.application.command.model.ImageMetadata;
import jp.i432kg.footprint.application.port.ImageMetadataExtractor;
import jp.i432kg.footprint.application.port.ImageStorage;
import jp.i432kg.footprint.domain.ObjectKeyFactory;
import jp.i432kg.footprint.domain.model.Location;
import jp.i432kg.footprint.domain.value.Byte;
import jp.i432kg.footprint.domain.value.FileExtension;
import jp.i432kg.footprint.domain.value.FileName;
import jp.i432kg.footprint.domain.value.ImageId;
import jp.i432kg.footprint.domain.value.Latitude;
import jp.i432kg.footprint.domain.value.Longitude;
import jp.i432kg.footprint.domain.value.ObjectKey;
import jp.i432kg.footprint.domain.value.Pixel;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.StorageObject;
import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.infrastructure.storage.S3ObjectResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Slf4j
@Repository
@ConditionalOnProperty(name = "app.storage.type", havingValue = "S3")
public class S3ImageRepositoryImpl implements ImageStorage, ImageMetadataExtractor {

    private final S3Client s3Client;
    private final S3ObjectResolver s3ObjectResolver;

    public S3ImageRepositoryImpl(
            final S3Client s3Client,
            final S3ObjectResolver s3ObjectResolver
    ) {
        this.s3Client = s3Client;
        this.s3ObjectResolver = s3ObjectResolver;
    }

    @Override
    public ImageMetadata extract(final StorageObject storageObject) throws ImageProcessingException, IOException {

        try {
            final String bucket = s3ObjectResolver.resolveBucket(storageObject);
            final String key = s3ObjectResolver.resolveKey(storageObject);

            final Long contentLength = s3Client.headObject(
                    HeadObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build()
            ).contentLength();

            try (ResponseInputStream<GetObjectResponse> s3Stream = s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build()
            )) {
                final byte[] imageBytes = s3Stream.readAllBytes();
                final Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(imageBytes));

                final Dimension dimension = extractDimensions(imageBytes);

                final Optional<GpsDirectory> gpsDir =
                        Optional.ofNullable(metadata.getFirstDirectoryOfType(GpsDirectory.class));
                final Location location = gpsDir
                        .filter(dir -> dir.getGeoLocation() != null)
                        .map(dir -> Location.of(
                                Latitude.of(BigDecimal.valueOf(dir.getGeoLocation().getLatitude())),
                                Longitude.of(BigDecimal.valueOf(dir.getGeoLocation().getLongitude()))
                        ))
                        .orElse(Location.unknown());

                final Optional<ExifSubIFDDirectory> subIfdDir =
                        Optional.ofNullable(metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class));
                final LocalDateTime takenAt = subIfdDir
                        .map(dir -> dir.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL))
                        .map(date -> date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                        .orElse(LocalDateTime.now());

                final String extensionStr = extractExtensionFromKey(key);
                final FileExtension fileExtension = FileExtension.of(extensionStr);

                final boolean hasEXIF =
                        metadata.containsDirectoryOfType(ExifIFD0Directory.class) || gpsDir.isPresent();

                final long fileSizeValue = contentLength != null ? contentLength : imageBytes.length;
                final Byte fileSize = Byte.of(fileSizeValue);

                return ImageMetadata.of(
                        fileExtension,
                        fileSize,
                        dimension.width(),
                        dimension.height(),
                        location,
                        hasEXIF,
                        takenAt
                );
            }

        } catch (NoSuchKeyException e) {
            log.error(
                    "S3 object not found. storageObjectKey={}",
                    storageObject.getObjectKey().getValue(),
                    e
            );
            throw new IOException("S3上の画像ファイルが見つかりません: " + storageObject.getObjectKey().getValue(), e);
        } catch (S3Exception e) {
            log.error(
                    "Failed to access S3 object. storageObjectKey={}",
                    storageObject.getObjectKey().getValue(),
                    e
            );
            throw new IOException("S3上の画像ファイルへのアクセスに失敗しました: " + storageObject.getObjectKey().getValue(), e);
        } catch (IllegalArgumentException e) {
            log.error(
                    "Failed to extract image metadata from S3. storageObjectKey={}",
                    storageObject.getObjectKey().getValue(),
                    e
            );
            throw new ImageProcessingException(
                    "画像メタデータの解析に失敗しました: " + storageObject.getObjectKey().getValue(),
                    e
            );
        }
    }

    @Override
    public StorageObject store(
            final InputStream imageStream,
            final FileName originalFilename,
            final UserId userId,
            final PostId postId
    ) throws IOException {

        try {
            final byte[] imageBytes = imageStream.readAllBytes();

            final FileType fileType;
            try (BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(imageBytes))) {
                fileType = FileTypeDetector.detectFileType(bis);
            }

            final String extensionStr = determineExtension(fileType, originalFilename);
            final FileExtension extension = FileExtension.of(extensionStr);

            final ImageId imageId = ImageId.of(UlidCreator.getUlid().toString());
            final ObjectKey objectKey = ObjectKeyFactory.createPostImageKey(userId, postId, imageId, extension);
            final StorageObject storageObject = StorageObject.s3(objectKey);

            final String bucket = s3ObjectResolver.resolveBucket(storageObject);
            final String key = s3ObjectResolver.resolveKey(storageObject);

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(resolveContentType(extension))
                            .contentLength((long) imageBytes.length)
                            .build(),
                    RequestBody.fromBytes(imageBytes)
            );

            return storageObject;
        } catch (IllegalArgumentException e) {
            log.error(
                    "Failed to save image to S3. userId={}, postId={}, originalFilename={}",
                    userId.getValue(),
                    postId.getValue(),
                    originalFilename.getValue(),
                    e
            );
            throw new IOException("サポートされていない画像形式です。", e);
        } catch (S3Exception e) {
            log.error(
                    "Failed to upload image to S3. userId={}, postId={}, originalFilename={}",
                    userId.getValue(),
                    postId.getValue(),
                    originalFilename.getValue(),
                    e
            );
            throw new IOException("S3への画像アップロードに失敗しました。", e);
        }
    }

    private String determineExtension(final FileType fileType, final FileName originalFilename)
            throws IllegalArgumentException {

        final Optional<FileExtension.Allowed> allowed = switch (fileType) {
            case Jpeg -> Optional.of(FileExtension.Allowed.JPG);
            case Png -> Optional.of(FileExtension.Allowed.PNG);
            case Gif -> Optional.of(FileExtension.Allowed.GIF);
            case WebP -> Optional.of(FileExtension.Allowed.WEBP);
            default -> Optional.empty();
        };

        return allowed
                .map(FileExtension.Allowed::getValue)
                .orElseGet(() -> {
                    final String name = originalFilename.getValue();
                    final int lastDotIndex = name.lastIndexOf(".");
                    final String ext = (lastDotIndex != -1)
                            ? name.substring(lastDotIndex + 1).toLowerCase()
                            : "";
                    return FileExtension.of(ext).getValue();
                });
    }

    private String resolveContentType(final FileExtension extension) {
        return switch (extension.getValue()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }

    private String extractExtensionFromKey(final String key) {
        final int lastDotIndex = key.lastIndexOf(".");
        if (lastDotIndex < 0 || lastDotIndex == key.length() - 1) {
            throw new IllegalArgumentException("拡張子をオブジェクトキーから取得できません: " + key);
        }
        return key.substring(lastDotIndex + 1);
    }

    private Dimension extractDimensions(final byte[] imageBytes) throws ImageProcessingException, IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
            final BufferedImage bufferedImage = ImageIO.read(bais);
            if (bufferedImage == null) {
                throw new ImageProcessingException("画像の幅・高さを取得できませんでした。");
            }
            return new Dimension(
                    Pixel.of(bufferedImage.getWidth()),
                    Pixel.of(bufferedImage.getHeight())
            );
        }
    }

    private record Dimension(Pixel width, Pixel height) {
    }
}
