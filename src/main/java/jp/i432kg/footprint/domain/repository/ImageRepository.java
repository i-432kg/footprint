package jp.i432kg.footprint.domain.repository;

import jp.i432kg.footprint.domain.model.Location;
import jp.i432kg.footprint.domain.value.ImageFileName;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ImageRepository {

    ImageFileName save(final MultipartFile file);

    Optional<Location> extractGpsLocation(final MultipartFile file);
}
