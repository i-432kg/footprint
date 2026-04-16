package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.exception.InvalidModelException;
import jp.i432kg.footprint.domain.value.Byte;
import jp.i432kg.footprint.domain.value.FileExtension;
import jp.i432kg.footprint.domain.value.Pixel;
import jp.i432kg.footprint.domain.value.StorageObject;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 画像ドメインモデル
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class Image {

    /**
     * 最大総ピクセル数：40MP (40,000,000 px)
     */
    private static final long MAX_TOTAL_PIXELS = 40_000_000L;

    /**
     * 最小短辺ピクセル数：320px
     */
    private static final int MIN_SHORT_SIDE_PIXELS = 320;

    /**
     * 最大辺ピクセル数：8192px
     */
    private static final int MAX_SIDE_PIXELS = 8192;

    /**
     * 画像のストレージ保存先
     */
    StorageObject storageObject;

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
     * @param storageObject 画像のストレージ保存先
     * @param fileExtension 画像の拡張子
     * @param fileSize      画像のファイルサイズ
     * @param width         画像の横幅
     * @param height        画像の高さ
     * @param location      位置情報
     * @param hasEXIF       EXIF 情報の有無
     * @param takenAt       撮影日時
     * @return {@link Image} インスタンス
     * @throws InvalidModelException バリデーションエラーの場合
     */
    public static Image of(
            final StorageObject storageObject,
            final FileExtension fileExtension,
            final Byte fileSize,
            final Pixel width,
            final Pixel height,
            final Location location,
            final boolean hasEXIF,
            final LocalDateTime takenAt
    ) {
        // 横幅のピクセル数チェック
        if (width.getValue() > MAX_SIDE_PIXELS) {
            throw InvalidModelException.invalid(
                    "image",
                    width.getValue(),
                    "width_pixels_exceed_limit"
            );
        }

        // 縦幅のピクセル数チェック
        if (height.getValue() > MAX_SIDE_PIXELS) {
            throw InvalidModelException.invalid(
                    "image",
                    height.getValue(),
                    "height_pixels_exceed_limit"
            );
        }

        // 短辺ピクセル数チェック
        final int shortSidePixels = Math.min(width.getValue(), height.getValue());
        if (shortSidePixels < MIN_SHORT_SIDE_PIXELS) {
            throw InvalidModelException.invalid(
                    "image",
                    shortSidePixels,
                    "short_side_pixels_too_small"
            );
        }

        // 総ピクセル数チェック
        final long totalPixels = (long) width.getValue() * height.getValue();
        if (totalPixels > MAX_TOTAL_PIXELS) {
            throw InvalidModelException.invalid(
                    "image",
                    totalPixels,
                    "total_pixels_exceed_limit"
            );
        }

        return new Image(storageObject, fileExtension, fileSize, width, height, location, takenAt, hasEXIF);
    }

    /**
     * 位置情報が有効かどうかを判定します
     */
    public boolean hasLocation() {
        return !location.isUnknown();
    }

}
