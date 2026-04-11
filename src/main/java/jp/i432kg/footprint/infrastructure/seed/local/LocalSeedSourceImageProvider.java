package jp.i432kg.footprint.infrastructure.seed.local;

import jp.i432kg.footprint.infrastructure.seed.shared.SeedSourceImage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Profile("local")
public class LocalSeedSourceImageProvider {

    public SeedSourceImage load(final String pathOrRelativePath) {
        try {
            final Path path = Paths.get(pathOrRelativePath).normalize();
            return new SeedSourceImage(Files.newInputStream(path), path.getFileName().toString());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load local seed source image. path=" + pathOrRelativePath, e);
        }
    }
}
