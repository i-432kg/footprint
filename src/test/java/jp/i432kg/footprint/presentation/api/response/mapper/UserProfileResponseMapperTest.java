package jp.i432kg.footprint.presentation.api.response.mapper;

import jp.i432kg.footprint.application.query.model.UserProfileSummary;
import jp.i432kg.footprint.presentation.api.response.UserProfileItemResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserProfileResponseMapperTest {

    private final UserProfileResponseMapper mapper = new UserProfileResponseMapper();

    @Test
    @DisplayName("UserProfileResponseMapper はプロフィールサマリーの全項目をレスポンスへ変換する")
    void should_mapUserProfileSummaryToResponse_when_summaryHasAllFields() {
        final UserProfileSummary summary = new UserProfileSummary(
                "user-01",
                "user_name",
                "user@example.com",
                3,
                5
        );

        final UserProfileItemResponse actual = mapper.from(summary);

        assertThat(actual.getId()).isEqualTo("user-01");
        assertThat(actual.getUsername()).isEqualTo("user_name");
        assertThat(actual.getEmail()).isEqualTo("user@example.com");
        assertThat(actual.getPostCount()).isEqualTo(3);
        assertThat(actual.getReplyCount()).isEqualTo(5);
    }

}
