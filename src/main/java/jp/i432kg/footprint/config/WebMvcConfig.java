package jp.i432kg.footprint.config;

import jp.i432kg.footprint.presentation.helper.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Spring MVC の設定をカスタマイズするための構成クラス。
 * 静的リソース（画像ファイル等）のマッピングや、コントローラーで使用する
 * 独自の型変換（Converter）の登録を行います。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final String storageLocation;

    private final String publicPath;

    private final LatitudeConverter latitudeConverter;

    private final LongitudeConverter longitudeConverter;

    private final PostIdConverter postIdConverter;

    private final ReplyIdConverter replyIdConverter;

    public WebMvcConfig(@Value("${storage.location}") String storageLocation,
                        @Value("${storage.public-path}") String publicPath,
                        LatitudeConverter latitudeConverter,
                        LongitudeConverter longitudeConverter,
                        PostIdConverter postIdConverter,
                        ReplyIdConverter replyIdConverter) {
        this.storageLocation = storageLocation;
        this.publicPath = publicPath;
        this.latitudeConverter = latitudeConverter;
        this.longitudeConverter = longitudeConverter;
        this.postIdConverter = postIdConverter;
        this.replyIdConverter = replyIdConverter;
    }

    /**
     * 外部ストレージに保存された画像ファイル等を HTTP 経由で公開するためのリソースハンドラーを登録します。
     *
     * @param registry リソースハンドラーのレジストリ
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path path = Paths.get(storageLocation);
        String absolutePath = path.toFile().getAbsolutePath();

        // 外部ストレージ（物理ディレクトリ）を Web のパスにマッピングする
        // "/images/**" へのリクエストを物理ディレクトリ "file:///.../upload-images/" にマッピング
        registry.addResourceHandler(publicPath + "**")
                .addResourceLocations("file:" + absolutePath + "/");
    }

    /**
     * リクエストパラメータやパス変数（@PathVariable）を独自の値オブジェクトに
     * 自動変換するための Converter を登録します。
     *
     * @param registry フォーマッターのレジストリ
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(latitudeConverter);
        registry.addConverter(longitudeConverter);
        registry.addConverter(postIdConverter);
        registry.addConverter(replyIdConverter);
    }
}
