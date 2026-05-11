package jp.i432kg.footprint.presentation.web;

import jp.i432kg.footprint.config.OgpProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class OgpModelAdviceTest {

    @Test
    @DisplayName("OgpModelAdvice.ogp は OGP 設定値を model に反映する")
    void should_createOgpModelWithConfiguredValues_when_ogpIsCalled() {
        final OgpProperties properties = properties();

        final OgpModel actual = new OgpModelAdvice(properties).ogp(request("https", "example.com", 443, "/about"));

        assertThat(actual.siteName()).isEqualTo("Footprint");
        assertThat(actual.title()).isEqualTo("Footprint - 写真と場所で日常を残す投稿アプリ");
        assertThat(actual.description()).isEqualTo("Footprint description");
        assertThat(actual.type()).isEqualTo("website");
        assertThat(actual.twitterCard()).isEqualTo("summary_large_image");
    }

    @Test
    @DisplayName("OgpModelAdvice.ogp は siteBaseUrl が設定されている場合に設定値を base URL として使う")
    void should_useConfiguredSiteBaseUrl_when_siteBaseUrlIsSet() {
        final OgpProperties properties = properties();
        properties.setSiteBaseUrl("https://example.com");

        final OgpModel actual = new OgpModelAdvice(properties).ogp(request("http", "localhost", 8080, "/about"));

        assertThat(actual.url()).isEqualTo("https://example.com/about");
        assertThat(actual.imageUrl()).isEqualTo("https://example.com/ogp.png");
    }

    @Test
    @DisplayName("OgpModelAdvice.ogp は siteBaseUrl が未設定の場合に request から base URL を組み立てる")
    void should_buildBaseUrlFromRequest_when_siteBaseUrlIsBlank() {
        final OgpProperties properties = properties();
        properties.setSiteBaseUrl("");

        final OgpModel actual = new OgpModelAdvice(properties).ogp(request("http", "localhost", 8080, "/about"));

        assertThat(actual.url()).isEqualTo("http://localhost:8080/about");
        assertThat(actual.imageUrl()).isEqualTo("http://localhost:8080/ogp.png");
    }

    @Test
    @DisplayName("OgpModelAdvice.ogp は HTTP 標準ポート 80 を省略する")
    void should_omitPort_when_requestUsesStandardHttpPort() {
        final OgpProperties properties = properties();
        properties.setSiteBaseUrl("");

        final OgpModel actual = new OgpModelAdvice(properties).ogp(request("http", "example.com", 80, "/about"));

        assertThat(actual.url()).isEqualTo("http://example.com/about");
    }

    @Test
    @DisplayName("OgpModelAdvice.ogp は HTTPS 標準ポート 443 を省略する")
    void should_omitPort_when_requestUsesStandardHttpsPort() {
        final OgpProperties properties = properties();
        properties.setSiteBaseUrl("");

        final OgpModel actual = new OgpModelAdvice(properties).ogp(request("https", "example.com", 443, "/about"));

        assertThat(actual.url()).isEqualTo("https://example.com/about");
    }

    @Test
    @DisplayName("OgpModelAdvice.ogp は HTTP 非標準ポートを付与する")
    void should_appendPort_when_requestUsesNonStandardHttpPort() {
        final OgpProperties properties = properties();
        properties.setSiteBaseUrl("");

        final OgpModel actual = new OgpModelAdvice(properties).ogp(request("http", "localhost", 8080, "/about"));

        assertThat(actual.url()).isEqualTo("http://localhost:8080/about");
    }

    @Test
    @DisplayName("OgpModelAdvice.ogp は HTTPS 非標準ポートを付与する")
    void should_appendPort_when_requestUsesNonStandardHttpsPort() {
        final OgpProperties properties = properties();
        properties.setSiteBaseUrl("");

        final OgpModel actual = new OgpModelAdvice(properties).ogp(request("https", "example.com", 8443, "/about"));

        assertThat(actual.url()).isEqualTo("https://example.com:8443/about");
    }

    @Test
    @DisplayName("OgpModelAdvice.ogp は siteBaseUrl 末尾のスラッシュを除去して URL を組み立てる")
    void should_removeTrailingSlash_when_siteBaseUrlEndsWithSlash() {
        final OgpProperties properties = properties();
        properties.setSiteBaseUrl("https://example.com/");

        final OgpModel actual = new OgpModelAdvice(properties).ogp(request("http", "localhost", 8080, "/about"));

        assertThat(actual.url()).isEqualTo("https://example.com/about");
        assertThat(actual.imageUrl()).isEqualTo("https://example.com/ogp.png");
    }

    @Test
    @DisplayName("OgpModelAdvice.ogp は imagePath に先頭スラッシュがない場合に補完する")
    void should_prefixSlash_when_imagePathDoesNotStartWithSlash() {
        final OgpProperties properties = properties();
        properties.setSiteBaseUrl("https://example.com");
        properties.setImagePath("ogp.png");

        final OgpModel actual = new OgpModelAdvice(properties).ogp(request("http", "localhost", 8080, "/about"));

        assertThat(actual.imageUrl()).isEqualTo("https://example.com/ogp.png");
    }

    @Test
    @DisplayName("OgpModelAdvice.ogp は imagePath が null の場合に root path を使う")
    void should_useRootPath_when_imagePathIsNull() {
        final OgpProperties properties = properties();
        properties.setSiteBaseUrl("https://example.com");
        properties.setImagePath(null);

        final OgpModel actual = new OgpModelAdvice(properties).ogp(request("http", "localhost", 8080, "/about"));

        assertThat(actual.imageUrl()).isEqualTo("https://example.com/");
    }

    @Test
    @DisplayName("OgpModelAdvice.ogp は imagePath が blank の場合に root path を使う")
    void should_useRootPath_when_imagePathIsBlank() {
        final OgpProperties properties = properties();
        properties.setSiteBaseUrl("https://example.com");
        properties.setImagePath("   ");

        final OgpModel actual = new OgpModelAdvice(properties).ogp(request("http", "localhost", 8080, "/about"));

        assertThat(actual.imageUrl()).isEqualTo("https://example.com/");
    }

    @Test
    @DisplayName("OgpModelAdvice.ogp は request の query string を og:url に含めない")
    void should_notIncludeQueryString_when_requestHasQueryString() {
        final OgpProperties properties = properties();
        properties.setSiteBaseUrl("https://example.com");
        final MockHttpServletRequest request = request("https", "example.com", 443, "/search");
        request.setQueryString("q=tokyo");

        final OgpModel actual = new OgpModelAdvice(properties).ogp(request);

        assertThat(actual.url()).isEqualTo("https://example.com/search");
    }

    private static OgpProperties properties() {
        final OgpProperties properties = new OgpProperties();
        properties.setSiteName("Footprint");
        properties.setTitle("Footprint - 写真と場所で日常を残す投稿アプリ");
        properties.setDescription("Footprint description");
        properties.setType("website");
        properties.setImagePath("/ogp.png");
        properties.setTwitterCard("summary_large_image");
        return properties;
    }

    private static MockHttpServletRequest request(
            final String scheme,
            final String serverName,
            final int serverPort,
            final String requestUri
    ) {
        final MockHttpServletRequest request = new MockHttpServletRequest("GET", requestUri);
        request.setScheme(scheme);
        request.setServerName(serverName);
        request.setServerPort(serverPort);
        return request;
    }

}
