package jp.i432kg.footprint.presentation.helper;

import jp.i432kg.footprint.domain.value.StorageObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageUrlConverter {

    private final String publicPath;

    public ImageUrlConverter(@Value("${storage.public-path}") String publicPath) {
        this.publicPath = publicPath;
    }

    public String convert(final StorageObject storageObject) {
        if (storageObject == null || storageObject.getObjectKey() == null) {
            return null;
        }
        if (!storageObject.isLocal()) {
            throw new IllegalArgumentException("storageObject is not LOCAL.");
        }
        return publicPath + storageObject.getObjectKey().getValue();
    }
}