package jp.i432kg.footprint.domain.service;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.exception.EmailAlreadyUsedException;
import jp.i432kg.footprint.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDomainServiceTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void isExistUser_shouldReturnRepositoryResult() {
        when(userRepository.existsById(DomainTestFixtures.userId())).thenReturn(true);
        UserDomainService service = new UserDomainService(userRepository);

        boolean actual = service.isExistUser(DomainTestFixtures.userId());

        assertThat(actual).isTrue();
    }

    @Test
    void ensureEmailNotAlreadyUsed_shouldDoNothing_whenEmailIsUnused() {
        when(userRepository.existsByEmail(DomainTestFixtures.user().getEmail())).thenReturn(false);
        UserDomainService service = new UserDomainService(userRepository);

        assertThatCode(() -> service.ensureEmailNotAlreadyUsed(DomainTestFixtures.user().getEmail()))
                .doesNotThrowAnyException();
    }

    @Test
    void ensureEmailNotAlreadyUsed_shouldThrowException_whenEmailIsAlreadyUsed() {
        when(userRepository.existsByEmail(DomainTestFixtures.user().getEmail())).thenReturn(true);
        UserDomainService service = new UserDomainService(userRepository);

        assertThatThrownBy(() -> service.ensureEmailNotAlreadyUsed(DomainTestFixtures.user().getEmail()))
                .isInstanceOf(EmailAlreadyUsedException.class)
                .hasMessageContaining("already used");
    }
}
