package jp.i432kg.footprint.infrastructure.storage;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import jp.i432kg.footprint.domain.model.Location;
import jp.i432kg.footprint.domain.repository.ImageRepository;
import jp.i432kg.footprint.domain.value.Coordinate;
import jp.i432kg.footprint.domain.value.ImageFileName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ImageRepositoryImpl implements ImageRepository {

    private final String storageLocation;

    // "finalフィールドにするためにコンストラクタで注入"
    public ImageRepositoryImpl(@Value("${storage.location}") String storageLocation) {
        this.storageLocation = storageLocation;
    }


    @Override
    public ImageFileName save(MultipartFile imageFile) {

        try {
            // "ファイル名の重複を避けるためUUIDを付与"
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path root = Paths.get(storageLocation);
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
            Files.copy(imageFile.getInputStream(), root.resolve(fileName));

            return new ImageFileName(fileName);
        } catch (IOException e) {
            throw new RuntimeException("ファイルの保存に失敗しました", e);
        }
    }

    @Override
    public Optional<Location> extractGpsLocation(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            Metadata metadata = ImageMetadataReader.readMetadata(is);
            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);

            if (gpsDirectory != null && gpsDirectory.getGeoLocation() != null) {
                double lat = gpsDirectory.getGeoLocation().getLatitude();
                double lon = gpsDirectory.getGeoLocation().getLongitude();
                return Optional.of(Location.builder()
                        .latitude(new Coordinate(lat))
                        .longitude(new Coordinate(lon))
                        .build());
            }
        } catch (Exception e) {
            System.err.println("EXIF読み取りに失敗しました: " + e.getMessage());
        }
        return Optional.empty();
    }
}
