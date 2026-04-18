package jp.i432kg.footprint.presentation.web;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.infrastructure.security.UserDetailsImpl;
import jp.i432kg.footprint.infrastructure.security.mapper.AuthMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;

import static org.assertj.core.api.Assertions.assertThat;

class RootControllerTest {

    private final RootController controller = new RootController();

    @Test
    @DisplayName("RootController は認証済みユーザーの displayUsername を model に追加する")
    void should_addDisplayUsernameToModel_when_userIsAuthenticated() {
        final ExtendedModelMap model = new ExtendedModelMap();

        controller.addLoginUserToModel(model, userDetails("alice"));

        assertThat(model.get("displayUsername")).isEqualTo("alice");
    }

    @Test
    @DisplayName("RootController は未認証時に model を変更しない")
    void should_notModifyModel_when_userIsNotAuthenticated() {
        final ExtendedModelMap model = new ExtendedModelMap();

        controller.addLoginUserToModel(model, null);

        assertThat(model).doesNotContainKey("displayUsername");
    }

    @Test
    @DisplayName("RootController は各画面の期待テンプレート名を返す")
    void should_returnExpectedTemplateNames_when_pageEndpointsAreCalled() {
        assertThat(controller.index()).isEqualTo("timeline");
        assertThat(controller.login()).isEqualTo("login");
        assertThat(controller.map()).isEqualTo("map");
        assertThat(controller.mypage()).isEqualTo("mypage");
        assertThat(controller.search()).isEqualTo("search");
        assertThat(controller.timeline()).isEqualTo("timeline");
    }

    private static UserDetailsImpl userDetails(final String displayUsername) {
        return UserDetailsImpl.fromEntity(
                new AuthMapper.AuthUserEntity(
                        DomainTestFixtures.userId(),
                        "user@example.com",
                        displayUsername,
                        "password"
                )
        );
    }

}
