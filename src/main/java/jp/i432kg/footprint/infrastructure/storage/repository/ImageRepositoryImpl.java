package jp.i432kg.footprint.infrastructure.storage.repository;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import jp.i432kg.footprint.domain.model.Image;
import jp.i432kg.footprint.domain.model.Location;
import jp.i432kg.footprint.domain.repository.ImageRepository;
import jp.i432kg.footprint.domain.value.*;
import jp.i432kg.footprint.domain.value.Byte;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

/**
 * 画像ファイルのリポジトリ実装クラス
 */
@Repository
public class ImageRepositoryImpl implements ImageRepository {

    private final String storageLocation;

    // final フィールドにするためにコンストラクタで注入
    public ImageRepositoryImpl(@Value("${storage.location}") String storageLocation) {
        this.storageLocation = storageLocation;
    }

    @Override
    public Image extractImageMetadata(final FilePath filePath) {
        try {
            final Path path = Paths.get(storageLocation).resolve(filePath.value());
            final Metadata metadata = ImageMetadataReader.readMetadata(path.toFile());

            // 1. 基本情報 (解像度) の取得
            // JpegDirectory から幅と高さを取得。取得できない場合は null を許容する
            final Optional<JpegDirectory> jpegDir =
                    Optional.ofNullable(metadata.getFirstDirectoryOfType(JpegDirectory.class));
            final Pixel width = jpegDir
                    .map(dir -> dir.getInteger(JpegDirectory.TAG_IMAGE_WIDTH))
                    .map(Pixel::of)
                    .orElse(null);
            final Pixel height = jpegDir
                    .map(dir -> dir.getInteger(JpegDirectory.TAG_IMAGE_HEIGHT))
                    .map(Pixel::of)
                    .orElse(null);

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
                    .orElse(LocalDateTime.now());

            // 4. ファイルシステムおよびその他の属性取得
            final Byte fileSize = Byte.of(Files.size(path));
            final String contentType = Files.probeContentType(path);
            final boolean hasEXIF = metadata.containsDirectoryOfType(ExifIFD0Directory.class) || gpsDir.isPresent();

            return Image.of(filePath, contentType, fileSize, width, height, location, hasEXIF, takenAt);

        } catch (final Exception e) {
            throw new RuntimeException("画像メタデータの抽出に失敗しました: " + filePath.value(), e);
        }
    }

    @Override
    public FileName save(final InputStream imageStream, final FileName originalFilename) {
        try {
            // ファイル名の競合を避けるため UUID をプレフィックスとして付与
            final FileName fileName = FileName.of(UUID.randomUUID() + "_" + originalFilename.value());
            final Path root = Paths.get(storageLocation);

            // 保存先ディレクトリが存在しない場合は作成する
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            // ストリームの内容を指定したパスにコピー（保存）する
            Files.copy(imageStream, root.resolve(fileName.value()));

            return fileName;
        } catch (final IOException e) {
            throw new RuntimeException("ファイルの物理保存に失敗しました", e);
        }
    }
}
