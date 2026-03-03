package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.value.Byte;
import jp.i432kg.footprint.domain.value.FilePath;
import jp.i432kg.footprint.domain.value.Pixel;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 画像ドメインモデル
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class Image {

    /**
     * 画像のファイルパス
     */
    FilePath filePath;

    /**
     * 画像の拡張子
     */
    String contentType;

    /**
     * 画像のファイルサイズ
     */
    Byte fileSize;

    /**
     * 画像の横幅
     */
    Pixel width;

    /**
     * 画像の高さ
     */
    Pixel height;

    /**
     * 画像の持っている位置情報
     */
    Location location;

    /**
     * 撮影日時
     */
    LocalDateTime takenAt;

    /**
     * 画像が EXIF 情報を持っているかどうか
     */
    boolean hasEXIF;

    /**
     * 画像ドメインモデルを生成します。
     *
     * @param filePath    画像のファイルパス
     * @param contentType 画像の MIME タイプ
     * @param fileSize    画像のファイルサイズ
     * @param width       画像の横幅
     * @param height      画像の高さ
     * @param location    位置情報
     * @param hasEXIF     EXIF 情報の有無
     * @param takenAt     撮影日時
     * @return {@link Image} インスタンス
     */
    public static Image of(
            final FilePath filePath,
            final String contentType,
            final Byte fileSize,
            final Pixel width,
            final Pixel height,
            final Location location,
            final boolean hasEXIF,
            final LocalDateTime takenAt
    ) {
        return new Image(filePath, contentType, fileSize, width, height, location, takenAt, hasEXIF);
    }

    /**
     * 位置情報が有効かどうかを判定します
     */
    public boolean hasLocation() {
        return !Objects.requireNonNull(location).equals(Location.unknown());
    }

}
