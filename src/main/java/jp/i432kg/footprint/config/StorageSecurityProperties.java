package jp.i432kg.footprint.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.stream.Stream;

/**
 * storage 配信に応じたセキュリティ設定値です。
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.storage")
public class StorageSecurityProperties {

    /**
     * CSP の {@code img-src} に追加で許可する origin のカンマ区切り一覧です。
     * 例:
     * {@code https://example.com,https://sample.com,etc...}
     */
    private String imageCspAllowOrigins = "";

    /**
     * {@code img-src} に追加する origin 一覧を返します。
     *
     * @return 空要素を除外した origin 一覧
     */
    public List<String> getImageCspAllowOriginList() {
        return Stream.of(imageCspAllowOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList();
    }
}
