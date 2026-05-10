package jp.i432kg.footprint.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * OGP / Twitter Card に出力するメタ情報の設定です。
 */
@Component
@ConfigurationProperties(prefix = "app.ogp")
@Getter
@Setter
public class OgpProperties {

    /**
     * アプリケーションの公開 origin です。
     * <p>
     * 未設定の場合はリクエスト URL から origin を組み立てます。
     */
    private String siteBaseUrl = "";

    /**
     * OGP の site_name です。
     */
    private String siteName = "Footprint";

    /**
     * OGP / Twitter Card の title です。
     */
    private String title = "Footprint - 写真と場所で日常を残す投稿アプリ";

    /**
     * OGP / Twitter Card の description です。
     */
    private String description = "Footprint は、写真付きの投稿をタイムラインと地図の両方から振り返れる Web アプリケーションです。";

    /**
     * OGP の type です。
     */
    private String type = "website";

    /**
     * OGP image の path です。
     */
    private String imagePath = "/ogp.png";

    /**
     * Twitter Card の card 種別です。
     */
    private String twitterCard = "summary_large_image";
}
