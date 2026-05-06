package jp.i432kg.footprint.domain.service;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.repository.PostRepository;
import jp.i432kg.footprint.domain.value.PostId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostDomainServiceTest {

    @Mock
    private PostRepository postRepository;

    @Test
    @DisplayName("PostDomainService.isExistPost は repository が true を返す場合に true を返す")
    void should_returnTrue_when_postExists() {
        final PostId postId = DomainTestFixtures.postId();
        final PostDomainService service = new PostDomainService(postRepository);
        when(postRepository.existsById(postId)).thenReturn(true);

        final boolean actual = service.isExistPost(postId);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("PostDomainService.isExistPost は repository が false を返す場合に false を返す")
    void should_returnFalse_when_postDoesNotExist() {
        final PostId postId = DomainTestFixtures.postId();
        final PostDomainService service = new PostDomainService(postRepository);
        when(postRepository.existsById(postId)).thenReturn(false);

        final boolean actual = service.isExistPost(postId);

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("PostDomainService.isExistPost は入力した PostId を repository に渡す")
    void should_delegateToRepositoryWithGivenPostId_when_checkingPostExistence() {
        final PostId postId = DomainTestFixtures.postId();
        final PostDomainService service = new PostDomainService(postRepository);
        when(postRepository.existsById(postId)).thenReturn(true);

        service.isExistPost(postId);

        verify(postRepository).existsById(postId);
    }
}
