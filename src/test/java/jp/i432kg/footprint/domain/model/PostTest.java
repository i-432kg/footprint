package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PostTest {

    @Test
    void of_shouldCreateInstanceWithGivenValues() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 4, 1, 13, 0);

        Post actual = Post.of(
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
