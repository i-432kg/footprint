package jp.i432kg.footprint.config;

import jp.i432kg.footprint.logging.operation.LoggingOperationInterceptor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Spring MVC の設定をカスタマイズするための構成クラス。
 * 静的リソース（画像ファイル等）のマッピングを行います。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final String storageType;
    private final String localRootDir;
    private final String imageBaseUrl;
    private final LoggingOperationInterceptor loggingOperationInterceptor;

    public WebMvcConfig(
            final LoggingOperationInterceptor loggingOperationInterceptor,
            @Value("${app.storage.type}") String storageType,
            @Value("${app.storage.local.root-dir:}") String localRootDir,
            @Value("${app.storage.image-base-url:/images/}") String imageBaseUrl) {
        this.loggingOperationInterceptor = loggingOperationInterceptor;
        this.storageType = storageType;
        this.localRootDir = localRootDir;
        this.imageBaseUrl = imageBaseUrl;
    }

    /**
     * controller ごとに宣言した logging operation を request 文脈へ設定する interceptor を登録します。
     *
     * @param registry interceptor レジストリ
     */
    @Override
    public void addInterceptors(@NonNull final InterceptorRegistry registry) {
        registry.addInterceptor(loggingOperationInterceptor);
    }

    /**
     * 外部ストレージに保存された画像ファイル等を HTTP 経由で公開するためのリソースハンドラーを登録します。
     *
     * @param registry リソースハンドラーのレジストリ
     */
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        if (!"LOCAL".equals(storageType)) {
            // S3 等を使用する場合は、Spring MVC 経由で配信しないため登録しない
            return;
        }
        // 外部ストレージ（物理ディレクトリ）を Web のパスにマッピングする
        // 例: "/images/**" へのリクエストを物理ディレクトリ "storage/local/" にマッピング
        String absolutePath = Paths.get(localRootDir).toAbsolutePath().normalize().toString();
        if (!absolutePath.endsWith("/")) {
            absolutePath += "/";
        }

        registry.addResourceHandler(imageBaseUrl + "**")
                .addResourceLocations("file:" + absolutePath)
                .setCachePeriod(0)
                .resourceChain(true);
    }
}
