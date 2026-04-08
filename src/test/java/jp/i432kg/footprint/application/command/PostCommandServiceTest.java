package jp.i432kg.footprint.application.command;

import jp.i432kg.footprint.application.command.model.CreatePostCommand;
import jp.i432kg.footprint.application.exception.resource.UserNotFoundException;
import jp.i432kg.footprint.application.port.ImageMetadataExtractor;
import jp.i432kg.footprint.application.port.ImageStorage;
import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.repository.PostRepository;
import jp.i432kg.footprint.domain.service.UserDomainService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCommandServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ImageStorage imageStorage;

    @Mock
    private ImageMetadataExtractor imageMetadataExtractor;

    @Mock
    private UserDomainService userDomainService;

    @Test
    void createPost_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        CreatePostCommand command = CreatePostCommand.of(
                DomainTestFixtures.userId(),
                DomainTestFixtures.caption(),
                new ByteArrayInputStream(new byte[]{1, 2, 3}),
                jp.i432kg.footprint.domain.value.FileName.of("image.jpg")
        );
        when(userDomainService.isExistUser(DomainTestFixtures.userId())).thenReturn(false);
        final PostCommandService service = new PostCommandService(
                postRepository,
                imageStorage,
                imageMetadataExtractor,
                userDomainService
        );

        assertThatThrownBy(() -> service.createPost(command))
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(imageStorage, imageMetadataExtractor);
    }
}
