package jp.i432kg.footprint.infrastructure.seed;

import java.io.IOException;
import java.io.InputStream;

/**
 * seed 用元画像の入力ストリームと元ファイル名を保持する値オブジェクト。
 */
public record SeedSourceImage(InputStream inputStream, String originalFilename) implements AutoCloseable {

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
