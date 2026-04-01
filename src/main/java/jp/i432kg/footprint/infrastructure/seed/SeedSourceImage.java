package jp.i432kg.footprint.infrastructure.seed;

import java.io.IOException;
import java.io.InputStream;

/**
 * seed 用元画像を表す値オブジェクト。
 * <p>
 * S3 などから取得した画像の入力ストリームと、元ファイル名を保持する。
 * 使用後は {@link #close()} で入力ストリームを確実に閉じる。
 * </p>
 */
public record SeedSourceImage(InputStream inputStream, String originalFilename) implements AutoCloseable {

    /**
     * 保持している入力ストリームを閉じる。
     *
     * @throws IOException 入力ストリームのクローズに失敗した場合
     */
    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}