package jp.i432kg.footprint.presentation.helper;

import jp.i432kg.footprint.domain.value.StorageObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.storage.type", havingValue = "LOCAL", matchIfMissing = true)
public class LocalImageUrlResolver implements ImageUrlResolver {

    @Value("${app.storage.image-base-url:/images/}")
    private String imageBaseUrl;

    @Override
    public String resolve(final StorageObject storageObject) {
        if (storageObject == null || storageObject.getObjectKey() == null) {
            return null;
        }

        if (!storageObject.isLocal()) {
            throw new IllegalArgumentException("storageObject is not LOCAL.");
        }

        return imageBaseUrl + storageObject.getObjectKey().getValue();
    }
}