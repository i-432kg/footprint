package jp.i432kg.footprint.domain.service;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostDomainServiceTest {

    @Mock
    private PostRepository postRepository;
    @Test
    void isExistPost_shouldReturnRepositoryResult() {
        when(postRepository.existsById(DomainTestFixtures.postId())).thenReturn(true);
        PostDomainService service = new PostDomainService(postRepository);

        boolean actual = service.isExistPost(DomainTestFixtures.postId());

        assertThat(actual).isTrue();
    }
}
