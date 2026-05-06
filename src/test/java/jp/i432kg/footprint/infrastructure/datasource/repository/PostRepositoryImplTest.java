package jp.i432kg.footprint.infrastructure.datasource.repository;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.infrastructure.datasource.mapper.repository.PostMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostRepositoryImplTest {

    @Mock
    private PostMapper postMapper;

    @Test
    @DisplayName("PostRepositoryImpl.existsById は投稿が存在する場合に true を返す")
    void should_returnTrue_when_postExists() {
        final PostId postId = DomainTestFixtures.postId();
        when(postMapper.countByPostId(postId)).thenReturn(1);

        final boolean actual = newRepository().existsById(postId);

        assertThat(actual).isTrue();
        verify(postMapper).countByPostId(postId);
        verifyNoMoreInteractions(postMapper);
    }

    @Test
    @DisplayName("PostRepositoryImpl.existsById は投稿が存在しない場合に false を返す")
    void should_returnFalse_when_postDoesNotExist() {
        final PostId postId = DomainTestFixtures.postId();
        when(postMapper.countByPostId(postId)).thenReturn(0);

        final boolean actual = newRepository().existsById(postId);

        assertThat(actual).isFalse();
        verify(postMapper).countByPostId(postId);
        verifyNoMoreInteractions(postMapper);
    }

    @Test
    @DisplayName("PostRepositoryImpl.savePost は投稿本体と画像情報を順に保存する")
    void should_savePostAndImage_when_savePostCalled() {
        final Post post = DomainTestFixtures.post();

        newRepository().savePost(post);

        final ArgumentCaptor<PostMapper.PostInsertEntity> postCaptor =
                ArgumentCaptor.forClass(PostMapper.PostInsertEntity.class);
        final ArgumentCaptor<PostMapper.PostImageInsertEntity> postImageCaptor =
                ArgumentCaptor.forClass(PostMapper.PostImageInsertEntity.class);
        final InOrder inOrder = inOrder(postMapper);

        inOrder.verify(postMapper).insertPosts(postCaptor.capture());
        inOrder.verify(postMapper).insertPostImages(postImageCaptor.capture());
        verifyNoMoreInteractions(postMapper);

        final PostMapper.PostInsertEntity postEntity = postCaptor.getValue();
        assertThat(postEntity.getId()).isNull();
        assertThat(postEntity.getPostId()).isEqualTo(post.getPostId());
        assertThat(postEntity.getUserId()).isEqualTo(post.getUserId());
        assertThat(postEntity.getCaption()).isEqualTo(post.getCaption());
        assertThat(postEntity.isHasLocation()).isEqualTo(post.getImage().hasLocation());
        assertThat(postEntity.getLatitude()).isEqualTo(post.getImage().getLocation().getLatitude());
        assertThat(postEntity.getLongitude()).isEqualTo(post.getImage().getLocation().getLongitude());
        assertThat(postEntity.getTakenAt()).isEqualTo(post.getImage().getTakenAt());
        assertThat(postEntity.getCreatedAt()).isEqualTo(post.getCreatedAt());
        assertThat(postEntity.getUpdatedAt()).isEqualTo(post.getCreatedAt());

        final PostMapper.PostImageInsertEntity postImageEntity = postImageCaptor.getValue();
        assertThat(postImageEntity.getId()).isNull();
        assertThat(postImageEntity.getPostId()).isEqualTo(post.getPostId());
        assertThat(postImageEntity.getSortOrder()).isZero();
        assertThat(postImageEntity.getStorageType()).isEqualTo(post.getImage().getStorageObject().getStorageType());
        assertThat(postImageEntity.getObjectKey()).isEqualTo(post.getImage().getStorageObject().getObjectKey());
        assertThat(postImageEntity.getFileExtension()).isEqualTo(post.getImage().getFileExtension());
        assertThat(postImageEntity.getSizeBytes()).isEqualTo(post.getImage().getFileSize());
        assertThat(postImageEntity.getWidth()).isEqualTo(post.getImage().getWidth());
        assertThat(postImageEntity.getHeight()).isEqualTo(post.getImage().getHeight());
        assertThat(postImageEntity.isExifAvailable()).isEqualTo(post.getImage().isHasEXIF());
        assertThat(postImageEntity.getCreatedAt()).isEqualTo(post.getCreatedAt());
    }

    @Test
    @DisplayName("PostRepositoryImpl.existsById は mapper 例外を再送出する")
    void should_rethrowException_when_existsByIdFails() {
        final PostId postId = DomainTestFixtures.postId();
        final RuntimeException expected = new RuntimeException("count failed");
        when(postMapper.countByPostId(postId)).thenThrow(expected);

        assertThatThrownBy(() -> newRepository().existsById(postId))
                .isSameAs(expected);

        verify(postMapper).countByPostId(postId);
        verifyNoMoreInteractions(postMapper);
    }

    @Test
    @DisplayName("PostRepositoryImpl.savePost は投稿本体保存失敗時に例外を再送出し画像保存を行わない")
    void should_rethrowExceptionAndSkipImageSave_when_insertPostsFails() {
        final Post post = DomainTestFixtures.post();
        final RuntimeException expected = new RuntimeException("insert post failed");
        doThrow(expected).when(postMapper).insertPosts(org.mockito.ArgumentMatchers.any());

        assertThatThrownBy(() -> newRepository().savePost(post))
                .isSameAs(expected);

        verify(postMapper).insertPosts(org.mockito.ArgumentMatchers.any());
        verify(postMapper, never()).insertPostImages(org.mockito.ArgumentMatchers.any());
        verifyNoMoreInteractions(postMapper);
    }

    @Test
    @DisplayName("PostRepositoryImpl.savePost は画像保存失敗時に例外を再送出する")
    void should_rethrowException_when_insertPostImagesFails() {
        final Post post = DomainTestFixtures.post();
        final RuntimeException expected = new RuntimeException("insert image failed");
        doThrow(expected).when(postMapper).insertPostImages(org.mockito.ArgumentMatchers.any());

        assertThatThrownBy(() -> newRepository().savePost(post))
                .isSameAs(expected);

        verify(postMapper).insertPosts(org.mockito.ArgumentMatchers.any());
        verify(postMapper).insertPostImages(org.mockito.ArgumentMatchers.any());
        verifyNoMoreInteractions(postMapper);
    }

    private PostRepositoryImpl newRepository() {
        return new PostRepositoryImpl(postMapper);
    }
}
