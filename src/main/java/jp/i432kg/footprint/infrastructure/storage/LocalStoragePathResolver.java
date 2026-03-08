package jp.i432kg.footprint.infrastructure.storage;

import jp.i432kg.footprint.domain.value.StorageObject;

import java.nio.file.Path;

public class LocalStoragePathResolver {

    private final Path baseDir;

    public LocalStoragePathResolver(Path baseDir) {
        this.baseDir = baseDir;
    }

    public Path resolve(StorageObject storageObject) {
        if (!storageObject.isLocal()) {
            throw new IllegalArgumentException("storageObject is not LOCAL.");
        }
        return baseDir.resolve(storageObject.getObjectKey().getValue()).normalize();
    }
}
