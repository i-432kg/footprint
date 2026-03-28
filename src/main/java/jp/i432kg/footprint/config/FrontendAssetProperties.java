package jp.i432kg.footprint.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.frontend")
@Getter
@Setter
public class FrontendAssetProperties {

    private Entries entries = new Entries();

    @Getter
    @Setter
    public static class Entries {
        private Asset login = new Asset();
        private Asset map = new Asset();
        private Asset mypage = new Asset();
        private Asset search = new Asset();
        private Asset timeline = new Asset();
    }

    @Getter
    @Setter
    public static class Asset {
        private String js = "";
        private List<String> css = new ArrayList<>();
    }
}