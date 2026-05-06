package jp.i432kg.footprint.application.command.model;

import jp.i432kg.footprint.domain.model.Location;
import jp.i432kg.footprint.domain.value.Byte;
import jp.i432kg.footprint.domain.value.FileExtension;
import jp.i432kg.footprint.domain.value.Pixel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * 画像ファイルから抽出したメタデータを表すアプリケーション用モデルです。
 * <p>
 * ストレージ保存とメタデータ抽出を domain から切り離す際の受け皿として利用します。
 * 画像の保存先自体は含めず、{@code Image} 生成に必要な解析結果のみを保持します。
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageMetadata {

    /**
     * 画像の拡張子
     */
    FileExtension fileExtension;

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
     * 画像の位置情報
     */
    Location location;

    /**
     * 画像が EXIF 情報を持つかどうか
     */
    boolean hasEXIF;

    /**
     * 画像の撮影日時
     */
    LocalDateTime takenAt;

    /**
     * {@link ImageMetadata} のインスタンスを生成します。
     *
     * @param fileExtension 画像の拡張子
     * @param fileSize 画像のファイルサイズ
     * @param width 画像の横幅
     * @param height 画像の高さ
     * @param location 画像の位置情報
     * @param hasEXIF 画像が EXIF 情報を持つかどうか
     * @param takenAt 画像の撮影日時
     * @return {@link ImageMetadata} インスタンス
     */
    public static ImageMetadata of(
            final FileExtension fileExtension,
            final Byte fileSize,
            final Pixel width,
            final Pixel height,
            final Location location,
            final boolean hasEXIF,
            final LocalDateTime takenAt
    ) {
        return new ImageMetadata(fileExtension, fileSize, width, height, location, hasEXIF, takenAt);
    }
}
