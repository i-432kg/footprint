package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.value.ImageFileName;
import lombok.*;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class Image {

    ImageFileName imageFileName;

}
