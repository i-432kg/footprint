package jp.i432kg.footprint.infrastructure.storage.repository;

import com.drew.imaging.FileType;
import com.drew.imaging.FileTypeDetector;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import jp.i432kg.footprint.application.command.model.ImageMetadata;
import jp.i432kg.footprint.application.port.id.ImageIdGenerator;
import jp.i432kg.footprint.application.port.storage.ImageMetadataExtractor;
import jp.i432kg.footprint.application.port.storage.ImageStorage;
import jp.i432kg.footprint.domain.exception.DomainException;
import jp.i432kg.footprint.domain.model.Location;
import jp.i432kg.footprint.domain.value.*;
import jp.i432kg.footprint.domain.value.Byte;
import jp.i432kg.footprint.logging.LoggingEvents;
import jp.i432kg.footprint.infrastructure.storage.LocalStoragePathResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * ローカルストレージ向けの画像保存・画像解析実装です。
 */
@Slf4j
@Repository
@ConditionalOnProperty(name = "app.storage.type", havingValue = "LOCAL")
public class LocalImageRepositoryImpl implements ImageStorage, ImageMetadataExtractor {

    private final Path storageRoot;
    private final LocalStoragePathResolver localStoragePathResolver;
    private final Clock clock;
    private final ImageIdGenerator imageIdGenerator;

    // final フィールドにするためにコンストラクタで注入
    public LocalImageRepositoryImpl(
            @Value("${app.storage.local.root-dir}") String storageLocation,
            LocalStoragePathResolver localStoragePathResolver,
            Clock clock,
            ImageIdGenerator imageIdGenerator) {
        this.storageRoot = Paths.get(storageLocation);
        this.localStoragePathResolver = localStoragePathResolver;
        this.clock = clock;
        this.imageIdGenerator = imageIdGenerator;
    }

    @Override
    public ImageMetadata extract(final StorageObject storageObject) throws ImageProcessingException, IOException {

        try {
            final Path path = localStoragePathResolver.resolve(storageObject);
            final Metadata metadata = ImageMetadataReader.readMetadata(path.toFile());

            // 1. 基本情報 (解像度) の取得
            final Dimension dimension = extractDimensions(path);

            // 2. 位置情報 (GPS) の取得
            // GpsDirectory から緯度・経度を取得。存在しない場合は Location.unknown() を返す
            final Optional<GpsDirectory> gpsDir =
                    Optional.ofNullable(metadata.getFirstDirectoryOfType(GpsDirectory.class));
            final Location location = gpsDir
                    .filter(dir -> dir.getGeoLocation() != null)
                    .map(dir -> Location.of(
                            Latitude.of(BigDecimal.valueOf(dir.getGeoLocation().getLatitude())),
                            Longitude.of(BigDecimal.valueOf(dir.getGeoLocation().getLongitude()))
                    ))
                    .orElse(Location.unknown());

            // 3. 撮影日時の取得
            // EXIF の TAG_DATETIME_ORIGINAL から取得。取得できない場合は現在日時をデフォルトとする
            final Optional<ExifSubIFDDirectory> subIfdDir =
                    Optional.ofNullable(metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class));
            final LocalDateTime takenAt = subIfdDir
                    .map(dir -> dir.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL))
                    .map(date -> date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .orElse(LocalDateTime.now(clock));

            // 4. ファイルシステムおよびその他の属性取得
            final Byte fileSize = Byte.of(Files.size(path));

            // Content-Type から拡張子を特定
            final String fileName = path.getFileName().toString();
            final String extensionStr = fileName.substring(fileName.lastIndexOf(".") + 1);
            final FileExtension fileExtension = FileExtension.of(extensionStr);

            final boolean hasEXIF = metadata.containsDirectoryOfType(ExifIFD0Directory.class) || gpsDir.isPresent();

            return ImageMetadata.of(
                    fileExtension,
                    fileSize,
                    dimension.width(),
                    dimension.height(),
                    location,
                    hasEXIF,
                    takenAt
            );

        } catch (IllegalArgumentException | DomainException e) {
            log.atError()
                    .addKeyValue("event", LoggingEvents.LOCAL_IMAGE_METADATA_EXTRACT_FAILED)
                    .addKeyValue("objectKey", storageObject.getObjectKey().getValue())
                    .setCause(e)
                    .log("Failed to extract local image metadata");
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
            // 1. 一時ファイルとして保存
            final String tempId = UUID.randomUUID().toString();
            final Path tempPath = storageRoot.resolve(tempId + ".tmp");

            if (!Files.exists(storageRoot)) {
                Files.createDirectories(storageRoot);
            }
            Files.copy(imageStream, tempPath);

            // 2. ファイル形式の判定
            final FileType fileType;
            try (InputStream is = Files.newInputStream(tempPath);
                 BufferedInputStream bis = new BufferedInputStream(is)) {
                fileType = FileTypeDetector.detectFileType(bis);
            }

            // 3. 正しい拡張子の特定
            final String extensionStr = determineExtension(fileType, originalFilename);
            final FileExtension extension = FileExtension.of(extensionStr);

            // 4. ドメインルールに基づいた ObjectKey の生成
            final ImageId imageId = imageIdGenerator.generate();
            final ObjectKey objectKey = ObjectKey.createPostImageKey(userId, postId, imageId, extension);

            final StorageObject storageObject = StorageObject.local(objectKey);
            final Path finalPath = localStoragePathResolver.resolve(storageObject);

            // 5. 保存先ディレクトリの作成と移動
            if (finalPath.getParent() != null) {
                Files.createDirectories(finalPath.getParent());
            }
            Files.move(tempPath, finalPath);

            return storageObject;
        } catch (IllegalArgumentException | DomainException e) {
            log.atError()
                    .addKeyValue("event", LoggingEvents.LOCAL_IMAGE_STORE_FAILED)
                    .addKeyValue("userId", userId.getValue())
                    .addKeyValue("postId", postId.getValue())
                    .setCause(e)
                    .log("Failed to store local image");
            throw new IOException("サポートされていない画像形式です。", e);
        } catch (IOException e) {
            log.atError()
                    .addKeyValue("event", LoggingEvents.LOCAL_IMAGE_STORE_FAILED)
                    .addKeyValue("userId", userId.getValue())
                    .addKeyValue("postId", postId.getValue())
                    .setCause(e)
                    .log("Failed to store local image");
            throw e;
        }
    }

    @Override
    public void delete(final StorageObject storageObject) throws IOException {
        final Path path = localStoragePathResolver.resolve(storageObject);
        Files.deleteIfExists(path);
    }

    private String determineExtension(final FileType fileType, final FileName originalFilename)
            throws IllegalArgumentException{
        // FileType からドメインの Allowed Enum へのマッピングを試みる
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
                    // 特定できなかった場合、元のファイル名の拡張子が許可されているか確認
                    final String name = originalFilename.getValue();
                    final int lastDotIndex = name.lastIndexOf(".");
                    final String ext = (lastDotIndex != -1) ? name.substring(lastDotIndex + 1).toLowerCase() : "";
                    return FileExtension.of(ext).getValue();
                });
    }

    /**
     * ローカル保存済み画像から幅・高さを抽出します。
     */
    private Dimension extractDimensions(final Path path) throws ImageProcessingException, IOException {
        try (InputStream inputStream = Files.newInputStream(path);
             ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(inputStream.readAllBytes())) {
            final BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
            if (Objects.isNull(bufferedImage)) {
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
