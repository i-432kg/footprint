package jp.i432kg.footprint.presentation.api.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserProfileItemResponseTest {

    @Test
    @DisplayName("UserProfileItemResponse は設定されたプロフィール情報を保持する")
    void should_holdValues_when_allFieldsAreSet() {
        final UserProfileItemResponse response = UserProfileItemResponse.of(
                "user-01",
                "user_name",
                "user@example.com",
                3,
                5
        );

        assertThat(response.getId()).isEqualTo("user-01");
        assertThat(response.getUsername()).isEqualTo("user_name");
        assertThat(response.getEmail()).isEqualTo("user@example.com");
        assertThat(response.getPostCount()).isEqualTo(3);
        assertThat(response.getReplyCount()).isEqualTo(5);
    }

}
