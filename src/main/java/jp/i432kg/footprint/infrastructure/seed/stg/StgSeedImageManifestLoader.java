package jp.i432kg.footprint.infrastructure.seed.stg;

import jp.i432kg.footprint.infrastructure.seed.shared.SeedImageManifestParser;
import jp.i432kg.footprint.infrastructure.seed.shared.SeedSourceImage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * S3 上の seed-images.json から seed 投稿に利用する画像オブジェクトキー一覧を読み込む。
 */
@Component
@Profile("stg")
@RequiredArgsConstructor
public class StgSeedImageManifestLoader {

    private final S3SeedSourceImageProvider seedSourceImageProvider;
    private final StgSeedProperties properties;
    private final SeedImageManifestParser manifestParser;

    /**
     * seed-images.json から画像オブジェクトキー一覧を読み込む。
     *
     * @return 重複を除去したオブジェクトキー一覧
     */
    public List<String> loadObjectKeys() {
        final String manifestObjectKey = properties.getManifestObjectKey();

        try (SeedSourceImage manifest = seedSourceImageProvider.load(manifestObjectKey);
             InputStream inputStream = manifest.inputStream()) {
            return manifestParser.parseEntries(inputStream, manifestObjectKey);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read seed image manifest. objectKey=" + manifestObjectKey, e);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Failed to load seed image manifest. objectKey=" + manifestObjectKey, e);
        }
    }
}
