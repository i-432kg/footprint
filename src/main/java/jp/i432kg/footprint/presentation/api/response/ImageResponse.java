package jp.i432kg.footprint.presentation.api.response;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * 投稿画像 1 件分を表す API レスポンス DTO です。
 */
@Value
@AllArgsConstructor(staticName = "of")
public class ImageResponse {
    /**
     * 画像 ID です。
     */
    String id;

    /**
     * 投稿内での画像表示順です。
     */
    Integer sortOrder;

    /**
     * 画像表示用 URL です。
     */
    String url;

    /**
     * 画像ファイルの拡張子です。
     */
    String fileExtension;

    /**
     * 画像ファイルサイズです。単位は byte です。
     */
    Long sizeBytes;

    /**
     * 画像の横幅です。単位は pixel です。
     */
    Integer width;

    /**
     * 画像の縦幅です。単位は pixel です。
     */
    Integer height;
}
