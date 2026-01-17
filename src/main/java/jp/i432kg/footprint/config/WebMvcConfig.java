package jp.i432kg.footprint.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final String storageLocation;

    private final String publicPath;

    public WebMvcConfig(@Value("${storage.location}") String storageLocation,
                        @Value("${storage.public-path}") String publicPath) {
        this.storageLocation = storageLocation;
        this.publicPath = publicPath;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path path = Paths.get(storageLocation);
        String absolutePath = path.toFile().getAbsolutePath();

        // "/images/**" へのリクエストを物理ディレクトリ "file:///.../upload-images/" にマッピング
        registry.addResourceHandler(publicPath + "**")
                .addResourceLocations("file:" + absolutePath + "/");
    }
}
