package jp.i432kg.footprint.application.command;

import com.drew.imaging.ImageProcessingException;
import jp.i432kg.footprint.application.command.model.CreatePostCommand;
import jp.i432kg.footprint.application.command.model.ImageMetadata;
import jp.i432kg.footprint.application.exception.resource.UserNotFoundException;
import jp.i432kg.footprint.application.exception.usecase.PostCommandFailedException;
import jp.i432kg.footprint.application.port.ImageMetadataExtractor;
import jp.i432kg.footprint.application.port.ImageStorage;
import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.repository.PostRepository;
import jp.i432kg.footprint.domain.service.UserDomainService;
import jp.i432kg.footprint.domain.value.FileName;
import jp.i432kg.footprint.domain.value.StorageObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
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
    @DisplayName("PostCommandService.createPost は依存処理が成功した場合に投稿を作成する")
    void should_createPost_when_dependenciesSucceed() throws Exception {
        final CreatePostCommand command = createPostCommand();
        final StorageObject storageObject = DomainTestFixtures.storageObject();
        final ImageMetadata metadata = ImageMetadata.of(
                DomainTestFixtures.fileExtension(),
                DomainTestFixtures.fileSize(),
                DomainTestFixtures.width(),
                DomainTestFixtures.height(),
                DomainTestFixtures.location(),
                true,
                LocalDateTime.of(2026, 4, 1, 12, 30)
        );
        when(userDomainService.isExistUser(command.getUserId())).thenReturn(true);
        when(imageStorage.store(
                eq(command.getImageStream()),
                eq(command.getOriginalFilename()),
                eq(command.getUserId()),
                any()
        )).thenReturn(storageObject);
        when(imageMetadataExtractor.extract(storageObject)).thenReturn(metadata);

        newService().createPost(command);

        final ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).savePost(captor.capture());
        final Post actual = captor.getValue();

        assertThat(actual.getUserId()).isEqualTo(command.getUserId());
        assertThat(actual.getCaption()).isEqualTo(command.getComment());
        assertThat(actual.getImage().getStorageObject()).isEqualTo(storageObject);
        assertThat(actual.getImage().getFileExtension()).isEqualTo(metadata.getFileExtension());
        assertThat(actual.getImage().getFileSize()).isEqualTo(metadata.getFileSize());
        assertThat(actual.getImage().getWidth()).isEqualTo(metadata.getWidth());
        assertThat(actual.getImage().getHeight()).isEqualTo(metadata.getHeight());
        assertThat(actual.getImage().getLocation()).isEqualTo(metadata.getLocation());
        assertThat(actual.getImage().isHasEXIF()).isEqualTo(metadata.isHasEXIF());
        assertThat(actual.getImage().getTakenAt()).isEqualTo(metadata.getTakenAt());
        assertThat(actual.getPostId()).isNotNull();
        assertThat(actual.getCreatedAt()).isNotNull();
        verify(imageStorage).store(command.getImageStream(), command.getOriginalFilename(), command.getUserId(), actual.getPostId());
        verify(imageMetadataExtractor).extract(storageObject);
    }

    @Test
    @DisplayName("PostCommandService.createPost はユーザーが存在しない場合に UserNotFoundException を送出する")
    void should_throwUserNotFoundException_when_userDoesNotExist() {
        final CreatePostCommand command = createPostCommand();
        when(userDomainService.isExistUser(command.getUserId())).thenReturn(false);

        assertThatThrownBy(() -> newService().createPost(command))
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(imageStorage, imageMetadataExtractor, postRepository);
    }

    @Test
    @DisplayName("PostCommandService.createPost は画像保存失敗を PostCommandFailedException に変換する")
    void should_throwUsecaseException_when_imageStoreFails() throws Exception {
        final CreatePostCommand command = createPostCommand();
        when(userDomainService.isExistUser(command.getUserId())).thenReturn(true);
        when(imageStorage.store(any(), any(), any(), any())).thenThrow(new IOException("save failed"));

        assertThatThrownBy(() -> newService().createPost(command))
                .isInstanceOf(PostCommandFailedException.class)
                .satisfies(throwable -> {
                    final PostCommandFailedException exception = (PostCommandFailedException) throwable;
                    assertThat(exception.getDetails())
                            .containsEntry("target", "image")
                            .containsEntry("reason", "image_save_failed")
                            .containsEntry("rejectedValue", command.getOriginalFilename().getValue());
                });

        verify(postRepository, never()).savePost(any());
        verify(imageStorage, never()).delete(any());
    }

    @Test
    @DisplayName("PostCommandService.createPost はメタデータ抽出失敗時に保存済み画像を削除する")
    void should_cleanupStoredImage_when_metadataExtractionFails() throws Exception {
        final CreatePostCommand command = createPostCommand();
        final StorageObject storageObject = DomainTestFixtures.storageObject();
        when(userDomainService.isExistUser(command.getUserId())).thenReturn(true);
        when(imageStorage.store(any(), any(), any(), any())).thenReturn(storageObject);
        when(imageMetadataExtractor.extract(storageObject)).thenThrow(new ImageProcessingException("broken"));

        assertThatThrownBy(() -> newService().createPost(command))
                .isInstanceOf(PostCommandFailedException.class)
                .satisfies(throwable -> {
                    final PostCommandFailedException exception = (PostCommandFailedException) throwable;
                    assertThat(exception.getDetails())
                            .containsEntry("target", "image")
                            .containsEntry("reason", "image_metadata_extract_failed")
                            .containsEntry("rejectedValue", storageObject.getObjectKey().getValue());
                });

        verify(imageStorage).delete(storageObject);
        verify(postRepository, never()).savePost(any());
    }

    @Test
    @DisplayName("PostCommandService.createPost は投稿保存失敗時に保存済み画像を削除する")
    void should_cleanupStoredImage_when_persistenceFails() throws Exception {
        final CreatePostCommand command = createPostCommand();
        final StorageObject storageObject = DomainTestFixtures.storageObject();
        final ImageMetadata metadata = ImageMetadata.of(
                DomainTestFixtures.fileExtension(),
                DomainTestFixtures.fileSize(),
                DomainTestFixtures.width(),
                DomainTestFixtures.height(),
                DomainTestFixtures.location(),
                true,
                LocalDateTime.of(2026, 4, 1, 12, 30)
        );
        when(userDomainService.isExistUser(command.getUserId())).thenReturn(true);
        when(imageStorage.store(any(), any(), any(), any())).thenReturn(storageObject);
        when(imageMetadataExtractor.extract(storageObject)).thenReturn(metadata);
        doThrow(new DataAccessResourceFailureException("db down")).when(postRepository).savePost(any());

        assertThatThrownBy(() -> newService().createPost(command))
                .isInstanceOf(PostCommandFailedException.class)
                .satisfies(throwable -> {
                    final PostCommandFailedException exception = (PostCommandFailedException) throwable;
                    assertThat(exception.getDetails())
                            .containsEntry("target", "post")
                            .containsEntry("reason", "persistence_error");
                });

        verify(imageStorage).delete(storageObject);
    }

    @Test
    @DisplayName("PostCommandService.createPost は cleanup 失敗時も元の例外を優先する")
    void should_prioritizeOriginalException_when_cleanupFails() throws Exception {
        final CreatePostCommand command = createPostCommand();
        final StorageObject storageObject = DomainTestFixtures.storageObject();
        when(userDomainService.isExistUser(command.getUserId())).thenReturn(true);
        when(imageStorage.store(any(), any(), any(), any())).thenReturn(storageObject);
        when(imageMetadataExtractor.extract(storageObject)).thenThrow(new IOException("extract failed"));
        doThrow(new IOException("cleanup failed")).when(imageStorage).delete(storageObject);

        assertThatThrownBy(() -> newService().createPost(command))
                .isInstanceOf(PostCommandFailedException.class)
                .hasCauseInstanceOf(IOException.class)
                .satisfies(throwable -> {
                    final PostCommandFailedException exception = (PostCommandFailedException) throwable;
                    assertThat(exception.getCause()).hasMessage("extract failed");
                    assertThat(exception.getDetails())
                            .containsEntry("target", "image")
                            .containsEntry("reason", "image_metadata_extract_failed");
                });

        verify(imageStorage).delete(storageObject);
    }

    private static CreatePostCommand createPostCommand() {
        return CreatePostCommand.of(
                DomainTestFixtures.userId(),
                DomainTestFixtures.caption(),
                new ByteArrayInputStream(new byte[]{1, 2, 3}),
                FileName.of("image.jpg")
        );
    }
}
