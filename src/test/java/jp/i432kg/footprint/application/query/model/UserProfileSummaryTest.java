package jp.i432kg.footprint.application.query.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserProfileSummaryTest {

    @Test
    @DisplayName("UserProfileSummary は設定されたプロフィール情報を保持する")
    void should_holdValues_when_allFieldsAreSet() {
        final UserProfileSummary summary = new UserProfileSummary(
                "user-01",
                "user_name",
                "user@example.com",
                10,
                20
        );

        assertThat(summary.getId()).isEqualTo("user-01");
        assertThat(summary.getUsername()).isEqualTo("user_name");
        assertThat(summary.getEmail()).isEqualTo("user@example.com");
        assertThat(summary.getPostCount()).isEqualTo(10);
        assertThat(summary.getReplyCount()).isEqualTo(20);
    }

    @Test
    @DisplayName("UserProfileSummary は no-args 生成時に既定値で初期化される")
    void should_initializeDefaultValues_when_createdWithNoArgsConstructor() {
        final UserProfileSummary summary = new UserProfileSummary();

        assertThat(summary.getId()).isNull();
        assertThat(summary.getUsername()).isNull();
        assertThat(summary.getEmail()).isNull();
        assertThat(summary.getPostCount()).isZero();
        assertThat(summary.getReplyCount()).isZero();
    }
}
