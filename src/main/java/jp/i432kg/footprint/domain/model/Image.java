package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.value.FilePath;
import lombok.*;

import java.time.LocalDateTime;

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
     * 撮影日時
     */
    LocalDateTime takenAt;

}
