package jp.i432kg.footprint.presentation.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FaviconControllerTest {

    private final FaviconController controller = new FaviconController();

    @Test
    @DisplayName("FaviconController は favicon.ico を favicon.svg へリダイレクトする")
    void should_redirectToSvgFavicon_when_faviconIcoIsRequested() {
        assertThat(controller.favicon()).isEqualTo("redirect:/favicon.svg");
    }
}
