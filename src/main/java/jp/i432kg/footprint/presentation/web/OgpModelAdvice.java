package jp.i432kg.footprint.presentation.web;

import jakarta.servlet.http.HttpServletRequest;
import jp.i432kg.footprint.config.OgpProperties;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * OGP / Twitter Card 用の共通 model を全画面へ公開する advice です。
 */
@ControllerAdvice
public class OgpModelAdvice {

    private final OgpProperties ogpProperties;

    public OgpModelAdvice(final OgpProperties ogpProperties) {
        this.ogpProperties = ogpProperties;
    }

    /**
     * OGP / Twitter Card 用の共通 model を構築します。
     *
     * @param request 現在の HTTP request
     * @return テンプレートから参照する OGP model
     */
    @ModelAttribute("ogp")
    public OgpModel ogp(final HttpServletRequest request) {
        final String baseUrl = resolveBaseUrl(request);
        final String path = request.getRequestURI();

        return OgpModel.of(
                ogpProperties.getSiteName(),
                ogpProperties.getTitle(),
                ogpProperties.getDescription(),
                ogpProperties.getType(),
                baseUrl + path,
                baseUrl + normalizePath(ogpProperties.getImagePath()),
                ogpProperties.getTwitterCard()
        );
    }

    private String resolveBaseUrl(final HttpServletRequest request) {
        if (ogpProperties.getSiteBaseUrl() != null && !ogpProperties.getSiteBaseUrl().isBlank()) {
            return removeTrailingSlash(ogpProperties.getSiteBaseUrl());
        }

        final StringBuilder baseUrl = new StringBuilder()
                .append(request.getScheme())
                .append("://")
                .append(request.getServerName());

        final int port = request.getServerPort();
        if (shouldAppendPort(request.getScheme(), port)) {
            baseUrl.append(":").append(port);
        }

        return baseUrl.toString();
    }

    private boolean shouldAppendPort(final String scheme, final int port) {
        return !("http".equals(scheme) && port == 80)
                && !("https".equals(scheme) && port == 443);
    }

    private String normalizePath(final String path) {
        if (path == null || path.isBlank()) {
            return "/";
        }

        return path.startsWith("/") ? path : "/" + path;
    }

    private String removeTrailingSlash(final String value) {
        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }

        return value;
    }
}
