package jp.i432kg.footprint.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Thymeleaf テンプレートから参照する frontend asset 設定です。
 * <p>
 * local 環境では Vite dev server 上の asset を参照し、stg/prod 環境では
 * Spring Boot の static resource に取り込まれた build 成果物を参照します。
 */
@Component
@ConfigurationProperties(prefix = "app.frontend")
@Getter
@Setter
public class FrontendAssetProperties {

    /**
     * favicon など、Vite の public directory 由来 asset の基底 URL です。
     * <p>
     * 空文字の場合は同一 origin の root-relative path として参照します。
     */
    private String publicBaseUrl = "";

    /**
     * 画面ごとの entry asset 設定です。
     */
    private Entries entries = new Entries();

    /**
     * public asset の参照 URL を返します。
     *
     * @param path public asset の path
     * @return publicBaseUrl を反映した asset URL
     */
    public String publicAssetUrl(final String path) {
        if (publicBaseUrl == null || publicBaseUrl.isBlank()) {
            return path;
        }

        // 設定値と呼び出し側 path のスラッシュ有無が揺れても URL が二重スラッシュにならないようにする。
        final String normalizedBaseUrl = publicBaseUrl.endsWith("/")
                ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1)
                : publicBaseUrl;
        final String normalizedPath = path.startsWith("/") ? path : "/" + path;
        return normalizedBaseUrl + normalizedPath;
    }

    /**
     * 画面ごとの frontend entry asset 設定です。
     */
    @Getter
    @Setter
    public static class Entries {
        /**
         * login 画面の asset 設定です。
         */
        private Asset login = new Asset();

        /**
         * map 画面の asset 設定です。
         */
        private Asset map = new Asset();

        /**
         * mypage 画面の asset 設定です。
         */
        private Asset mypage = new Asset();

        /**
         * search 画面の asset 設定です。
         */
        private Asset search = new Asset();

        /**
         * timeline 画面の asset 設定です。
         */
        private Asset timeline = new Asset();
    }

    /**
     * 1 画面分の JS/CSS asset 設定です。
     */
    @Getter
    @Setter
    public static class Asset {
        /**
         * entry JS の URL です。
         */
        private String js = "";

        /**
         * entry JS に対応する CSS の URL 一覧です。
         */
        private List<String> css = new ArrayList<>();
    }
}
