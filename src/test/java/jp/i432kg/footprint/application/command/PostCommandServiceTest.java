package jp.i432kg.footprint.application.command;

import jp.i432kg.footprint.application.command.model.CreatePostCommand;
import jp.i432kg.footprint.application.exception.resource.UserNotFoundException;
import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.repository.ImageRepository;
import jp.i432kg.footprint.domain.repository.PostRepository;
import jp.i432kg.footprint.domain.service.PostDomainService;
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
    private ImageRepository imageRepository;

    @Mock
    private PostDomainService postDomainService;

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
        PostCommandService service = new PostCommandService(
                postRepository,
                imageRepository,
                userDomainService
        );

        assertThatThrownBy(() -> service.createPost(command))
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(imageRepository);
    }
}
