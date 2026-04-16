package jp.i432kg.footprint.domain.service;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.exception.EmailAlreadyUsedException;
import jp.i432kg.footprint.domain.repository.UserRepository;
import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.domain.value.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDomainServiceTest {

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("UserDomainService.isExistUser は repository が true を返す場合に true を返す")
    void should_returnTrue_when_userExists() {
        final UserId userId = DomainTestFixtures.userId();
        final UserDomainService service = new UserDomainService(userRepository);
        when(userRepository.existsById(userId)).thenReturn(true);

        final boolean actual = service.isExistUser(userId);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("UserDomainService.isExistUser は repository が false を返す場合に false を返す")
    void should_returnFalse_when_userDoesNotExist() {
        final UserId userId = DomainTestFixtures.userId();
        final UserDomainService service = new UserDomainService(userRepository);
        when(userRepository.existsById(userId)).thenReturn(false);

        final boolean actual = service.isExistUser(userId);

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("UserDomainService.ensureEmailNotAlreadyUsed はメールアドレスが未使用の場合に例外を送出しない")
    void should_notThrowException_when_emailIsNotUsed() {
        final EmailAddress email = DomainTestFixtures.user().getEmail();
        final UserDomainService service = new UserDomainService(userRepository);
        when(userRepository.existsByEmail(email)).thenReturn(false);

        assertThatCode(() -> service.ensureEmailNotAlreadyUsed(email))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("UserDomainService.ensureEmailNotAlreadyUsed はメールアドレスが使用済みの場合に重複利用例外を送出する")
    void should_throwEmailAlreadyUsedException_when_emailIsAlreadyUsed() {
        final EmailAddress email = DomainTestFixtures.user().getEmail();
        final UserDomainService service = new UserDomainService(userRepository);
        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThatThrownBy(() -> service.ensureEmailNotAlreadyUsed(email))
                .isInstanceOf(EmailAlreadyUsedException.class)
                .hasMessageContaining("already used");
    }

    @Test
    @DisplayName("UserDomainService は入力した UserId と EmailAddress を repository に渡す")
    void should_delegateToRepositoryWithGivenArguments_when_checkingUserOrEmail() {
        final UserId userId = DomainTestFixtures.userId();
        final EmailAddress email = DomainTestFixtures.user().getEmail();
        final UserDomainService service = new UserDomainService(userRepository);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.existsByEmail(email)).thenReturn(false);

        service.isExistUser(userId);
        service.ensureEmailNotAlreadyUsed(email);

        verify(userRepository).existsById(userId);
        verify(userRepository).existsByEmail(email);
    }
}
