package jp.i432kg.footprint.presentation.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OgpModelTest {

    @Test
    @DisplayName("OgpModel.of は OGP と Twitter Card の値を保持する")
    void should_createModel_when_valuesAreGiven() {
        final OgpModel actual = OgpModel.of(
                "Footprint",
                "Footprint - 写真と場所で日常を残す投稿アプリ",
                "Footprint description",
                "website",
                "https://example.com/about",
                "https://example.com/ogp.png",
                "summary_large_image"
        );

        assertThat(actual.siteName()).isEqualTo("Footprint");
        assertThat(actual.title()).isEqualTo("Footprint - 写真と場所で日常を残す投稿アプリ");
        assertThat(actual.description()).isEqualTo("Footprint description");
        assertThat(actual.type()).isEqualTo("website");
        assertThat(actual.url()).isEqualTo("https://example.com/about");
        assertThat(actual.imageUrl()).isEqualTo("https://example.com/ogp.png");
        assertThat(actual.twitterCard()).isEqualTo("summary_large_image");
    }

    @Test
    @DisplayName("OgpModel.of は空文字をそのまま保持する")
    void should_keepEmptyValues_when_emptyValuesAreGiven() {
        final OgpModel actual = OgpModel.of("", "", "", "", "", "", "");

        assertThat(actual.siteName()).isEmpty();
        assertThat(actual.title()).isEmpty();
        assertThat(actual.description()).isEmpty();
        assertThat(actual.type()).isEmpty();
        assertThat(actual.url()).isEmpty();
        assertThat(actual.imageUrl()).isEmpty();
        assertThat(actual.twitterCard()).isEmpty();
    }

    @Test
    @DisplayName("OgpModel.of は null をそのまま保持する")
    void should_keepNullValues_when_nullValuesAreGiven() {
        final OgpModel actual = OgpModel.of(null, null, null, null, null, null, null);

        assertThat(actual.siteName()).isNull();
        assertThat(actual.title()).isNull();
        assertThat(actual.description()).isNull();
        assertThat(actual.type()).isNull();
        assertThat(actual.url()).isNull();
        assertThat(actual.imageUrl()).isNull();
        assertThat(actual.twitterCard()).isNull();
    }

}
