package jp.i432kg.footprint.presentation.helper;

import jp.i432kg.footprint.domain.value.ImageFileName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageUrlConverter {

    private final String publicPath;

    public ImageUrlConverter(@Value("${storage.public-path}") String publicPath) {
        this.publicPath = publicPath;
    }

    public String convert(ImageFileName fileName) {
        if (fileName == null || fileName.value() == null) {
            return null;
        }
        return publicPath + fileName.value();
    }
}
