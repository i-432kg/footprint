package jp.i432kg.footprint.application.command;

import com.drew.imaging.ImageProcessingException;
import jp.i432kg.footprint.application.command.model.CreatePostCommand;
import jp.i432kg.footprint.application.exception.resource.UserNotFoundException;
import jp.i432kg.footprint.application.exception.usecase.PostCommandFailedException;
import jp.i432kg.footprint.application.port.ImageMetadataExtractor;
import jp.i432kg.footprint.application.port.ImageStorage;
import jp.i432kg.footprint.application.command.model.ImageMetadata;
import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.repository.PostRepository;
import jp.i432kg.footprint.domain.service.UserDomainService;
import org.springframework.dao.DataAccessResourceFailureException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
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

    private PostCommandService newService() {
        return new PostCommandService(
                postRepository,
                imageStorage,
                imageMetadataExtractor,
                userDomainService
        );
    }

    @Test
    void createPost_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        CreatePostCommand command = CreatePostCommand.of(
                DomainTestFixtures.userId(),
                DomainTestFixtures.caption(),
                new ByteArrayInputStream(new byte[]{1, 2, 3}),
                jp.i432kg.footprint.domain.value.FileName.of("image.jpg")
        );
        when(userDomainService.isExistUser(DomainTestFixtures.userId())).thenReturn(false);
        final PostCommandService service = newService();

        assertThatThrownBy(() -> service.createPost(command))
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(imageStorage, imageMetadataExtractor);
    }

    @Test
    void createPost_shouldDeleteStoredImage_whenMetadataExtractionFails() throws Exception {
        final CreatePostCommand command = CreatePostCommand.of(
                DomainTestFixtures.userId(),
                DomainTestFixtures.caption(),
                new ByteArrayInputStream(new byte[]{1, 2, 3}),
                jp.i432kg.footprint.domain.value.FileName.of("image.jpg")
        );
        final var storageObject = DomainTestFixtures.storageObject();

        when(userDomainService.isExistUser(DomainTestFixtures.userId())).thenReturn(true);
        when(imageStorage.store(any(), any(), any(), any())).thenReturn(storageObject);
        when(imageMetadataExtractor.extract(storageObject)).thenThrow(new ImageProcessingException("broken"));

        assertThatThrownBy(() -> newService().createPost(command))
                .isInstanceOf(PostCommandFailedException.class);

        verify(imageStorage).delete(storageObject);
    }

    @Test
    void createPost_shouldDeleteStoredImage_whenPersistenceFails() throws Exception {
        final CreatePostCommand command = CreatePostCommand.of(
                DomainTestFixtures.userId(),
                DomainTestFixtures.caption(),
                new ByteArrayInputStream(new byte[]{1, 2, 3}),
                jp.i432kg.footprint.domain.value.FileName.of("image.jpg")
        );
        final var storageObject = DomainTestFixtures.storageObject();
        final var metadata = ImageMetadata.of(
                DomainTestFixtures.fileExtension(),
                DomainTestFixtures.fileSize(),
                DomainTestFixtures.width(),
                DomainTestFixtures.height(),
                DomainTestFixtures.location(),
                true,
                java.time.LocalDateTime.of(2026, 4, 1, 12, 30)
        );

        when(userDomainService.isExistUser(DomainTestFixtures.userId())).thenReturn(true);
        when(imageStorage.store(any(), any(), any(), any())).thenReturn(storageObject);
        when(imageMetadataExtractor.extract(storageObject)).thenReturn(metadata);
        doThrow(new DataAccessResourceFailureException("db down")).when(postRepository).savePost(any());

        assertThatThrownBy(() -> newService().createPost(command))
                .isInstanceOf(PostCommandFailedException.class);

        verify(imageStorage).delete(storageObject);
    }
}
