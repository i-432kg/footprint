package jp.i432kg.footprint.infrastructure.storage;

import jp.i432kg.footprint.domain.value.StorageObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@ConditionalOnProperty(name = "app.storage.type", havingValue = "LOCAL")
public class LocalStoragePathResolver {

    private final Path baseDir;

    public LocalStoragePathResolver(@Value("${app.storage.local.root-dir}") String baseDir) {
        this.baseDir = Paths.get(baseDir);
    }

    public Path resolve(StorageObject storageObject) {
        if (!storageObject.isLocal()) {
            throw new IllegalArgumentException("storageObject is not LOCAL.");
        }
        return baseDir.resolve(storageObject.getObjectKey().getValue()).normalize();
    }
}
