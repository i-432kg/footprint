package jp.i432kg.footprint.domain.service;

import jp.i432kg.footprint.application.exception.resource.UserNotFoundException;
import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostDomainServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserDomainService userDomainService;

    @Test
    void isExistPost_shouldReturnRepositoryResult() {
        when(postRepository.existsById(DomainTestFixtures.postId())).thenReturn(true);
        PostDomainService service = new PostDomainService(postRepository, userDomainService);

        boolean actual = service.isExistPost(DomainTestFixtures.postId());

        assertThat(actual).isTrue();
    }

    @Test
    void validateCreatePost_shouldDoNothing_whenUserExists() {
        when(userDomainService.isExistUser(DomainTestFixtures.userId())).thenReturn(true);
        PostDomainService service = new PostDomainService(postRepository, userDomainService);

        assertThatCode(() -> service.validateCreatePost(DomainTestFixtures.userId()))
                .doesNotThrowAnyException();
    }

    @Test
    void validateCreatePost_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        when(userDomainService.isExistUser(DomainTestFixtures.userId())).thenReturn(false);
        PostDomainService service = new PostDomainService(postRepository, userDomainService);

        assertThatThrownBy(() -> service.validateCreatePost(DomainTestFixtures.userId()))
                .isInstanceOf(UserNotFoundException.class);
    }
}
