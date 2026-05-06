package jp.i432kg.footprint.application.command.model;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.value.FileName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class CreatePostCommandTest {

    @Test
    @DisplayName("CreatePostCommand.of は渡された値を保持する")
    void should_createCommand_when_valuesAreProvided() {
        final InputStream imageStream = new ByteArrayInputStream(new byte[]{1, 2, 3});
        final FileName originalFilename = FileName.of("image.jpg");

        final CreatePostCommand actual = CreatePostCommand.of(
                DomainTestFixtures.userId(),
                DomainTestFixtures.caption(),
                imageStream,
                originalFilename
        );

        assertThat(actual.getUserId()).isEqualTo(DomainTestFixtures.userId());
        assertThat(actual.getComment()).isEqualTo(DomainTestFixtures.caption());
        assertThat(actual.getImageStream()).isSameAs(imageStream);
        assertThat(actual.getOriginalFilename()).isEqualTo(originalFilename);
    }
}
