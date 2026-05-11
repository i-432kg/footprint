package jp.i432kg.footprint.presentation.web;

/**
 * Thymeleaf テンプレートへ公開する OGP / Twitter Card 用 model です。
 *
 * @param siteName OGP site_name
 * @param title OGP / Twitter Card title
 * @param description OGP / Twitter Card description
 * @param type OGP type
 * @param url ページの絶対 URL
 * @param imageUrl OGP image の絶対 URL
 * @param twitterCard Twitter Card 種別
 */
public record OgpModel(
        String siteName,
        String title,
        String description,
        String type,
        String url,
        String imageUrl,
        String twitterCard
) {

    /**
     * OGP / Twitter Card 用 model を生成します。
     *
     * @param siteName OGP site_name
     * @param title OGP / Twitter Card title
     * @param description OGP / Twitter Card description
     * @param type OGP type
     * @param url ページの絶対 URL
     * @param imageUrl OGP image の絶対 URL
     * @param twitterCard Twitter Card 種別
     * @return OGP / Twitter Card 用 model
     */
    public static OgpModel of(
            final String siteName,
            final String title,
            final String description,
            final String type,
            final String url,
            final String imageUrl,
            final String twitterCard
    ) {
        return new OgpModel(siteName, title, description, type, url, imageUrl, twitterCard);
    }
}
