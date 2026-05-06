package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PostTest {

    @Test
    @DisplayName("Post.of は妥当な値から投稿を生成できる")
    void should_createPost_when_valuesAreValid() {
        final Post actual = Post.of(
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                DomainTestFixtures.image(),
                DomainTestFixtures.caption(),
                LocalDateTime.of(2026, 4, 1, 13, 0)
        );

        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("Post.of は入力した値を保持する")
    void should_keepGivenValues_when_postIsCreated() {
        final LocalDateTime createdAt = LocalDateTime.of(2026, 4, 1, 13, 0);

        final Post actual = Post.of(
                DomainTestFixtures.postId(),
                DomainTestFixtures.userId(),
                DomainTestFixtures.image(),
                DomainTestFixtures.caption(),
                createdAt
        );

        assertThat(actual.getPostId()).isEqualTo(DomainTestFixtures.postId());
        assertThat(actual.getUserId()).isEqualTo(DomainTestFixtures.userId());
        assertThat(actual.getImage()).isEqualTo(DomainTestFixtures.image());
        assertThat(actual.getCaption()).isEqualTo(DomainTestFixtures.caption());
        assertThat(actual.getCreatedAt()).isEqualTo(createdAt);
    }
}
