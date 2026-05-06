package jp.i432kg.footprint.application.command.model;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ImageMetadataTest {

    @Test
    @DisplayName("ImageMetadata.of は渡された解析結果を保持する")
    void should_createMetadata_when_valuesAreProvided() {
        final LocalDateTime takenAt = LocalDateTime.of(2026, 4, 1, 12, 30);

        final ImageMetadata actual = ImageMetadata.of(
                DomainTestFixtures.fileExtension(),
                DomainTestFixtures.fileSize(),
                DomainTestFixtures.width(),
                DomainTestFixtures.height(),
                DomainTestFixtures.location(),
                true,
                takenAt
        );

        assertThat(actual.getFileExtension()).isEqualTo(DomainTestFixtures.fileExtension());
        assertThat(actual.getFileSize()).isEqualTo(DomainTestFixtures.fileSize());
        assertThat(actual.getWidth()).isEqualTo(DomainTestFixtures.width());
        assertThat(actual.getHeight()).isEqualTo(DomainTestFixtures.height());
        assertThat(actual.getLocation()).isEqualTo(DomainTestFixtures.location());
        assertThat(actual.isHasEXIF()).isTrue();
        assertThat(actual.getTakenAt()).isEqualTo(takenAt);
    }
}
