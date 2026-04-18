package jp.i432kg.footprint.presentation.helper;

import jp.i432kg.footprint.domain.value.StorageObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * LOCAL 保存画像に対する公開 URL を解決する resolver です。
 */
@Component
@ConditionalOnProperty(name = "app.storage.type", havingValue = "LOCAL", matchIfMissing = true)
public class LocalImageUrlResolver implements ImageUrlResolver {

    @Value("${app.storage.image-base-url:/images/}")
    private String imageBaseUrl;

    /**
     * LOCAL 保存画像の表示 URL を解決します。
     *
     * @param storageObject LOCAL 保存の {@link StorageObject}
     * @return 表示 URL
     * @throws NullPointerException storageObject が {@code null} の場合
     * @throws IllegalArgumentException S3 など LOCAL 以外の保存種別が渡された場合
     */
    @Override
    public String resolve(final StorageObject storageObject) {

        if (!storageObject.isLocal()) {
            throw new IllegalArgumentException("storageObject is not LOCAL.");
        }

        return imageBaseUrl + storageObject.getObjectKey().getValue();
    }
}
