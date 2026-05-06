package jp.i432kg.footprint.infrastructure.seed.local;

import jp.i432kg.footprint.infrastructure.seed.shared.SeedSourceImageLoader;
import jp.i432kg.footprint.infrastructure.seed.shared.SeedSourceImage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * local seed 用の元画像をローカルファイルシステムから取得するコンポーネントです。
 */
@Component
@Profile("local")
public class LocalSeedSourceImageProvider implements SeedSourceImageLoader {

    /**
     * 指定パスの画像を開き、seed 投稿で利用できる {@link SeedSourceImage} を返します。
     *
     * @param pathOrRelativePath 画像ファイルパス
     * @return seed 用元画像
     */
    public SeedSourceImage load(final String pathOrRelativePath) {
        try {
            final Path path = Paths.get(pathOrRelativePath).normalize();
            return new SeedSourceImage(Files.newInputStream(path), path.getFileName().toString());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load local seed source image. path=" + pathOrRelativePath, e);
        }
    }
}
