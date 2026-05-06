package jp.i432kg.footprint.application.command;

import jp.i432kg.footprint.application.command.model.CreateUserCommand;
import jp.i432kg.footprint.application.command.service.UserCommandService;
import jp.i432kg.footprint.application.exception.usecase.UserCommandFailedException;
import jp.i432kg.footprint.application.port.id.UserIdGenerator;
import jp.i432kg.footprint.domain.exception.EmailAlreadyUsedException;
import jp.i432kg.footprint.domain.model.User;
import jp.i432kg.footprint.domain.repository.UserRepository;
import jp.i432kg.footprint.domain.service.UserDomainService;
import jp.i432kg.footprint.domain.value.BirthDate;
import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.domain.value.RawPassword;
import jp.i432kg.footprint.domain.value.UserName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceTest {

    private static final String FIXED_USER_ID = "01ARZ3NDEKTSV4RRFFQ69G5FAV";

    @Mock
    private UserDomainService userDomainService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserCommandService newService() {
        return newService(fixedUserIdGenerator());
    }

    private UserCommandService newService(final UserIdGenerator userIdGenerator) {
        return new UserCommandService(
                userDomainService,
                userRepository,
                passwordEncoder,
                userIdGenerator
        );
    }

    @Test
    @DisplayName("UserCommandService.createUser は重複確認とパスワードハッシュ化後にユーザーを保存する")
    void should_createUser_when_dependenciesSucceed() {
        final CreateUserCommand command = createUserCommand();
        when(passwordEncoder.encode(command.getRawPassword().getValue())).thenReturn("encoded-password");

        newService().createUser(command);

        final ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveUser(captor.capture());
        final User actual = captor.getValue();

        assertThat(actual.getUserId().getValue()).isEqualTo(FIXED_USER_ID);
        assertThat(actual.getUserName()).isEqualTo(command.getUserName());
        assertThat(actual.getEmail()).isEqualTo(command.getEmail());
        assertThat(actual.getBirthDate()).isEqualTo(command.getBirthDate());
        assertThat(actual.getHashedPassword().getValue()).isEqualTo("encoded-password");
        verify(userDomainService).ensureEmailNotAlreadyUsed(command.getEmail());
    }

    @Test
    @DisplayName("UserCommandService.createUser は同一 username でも email が異なれば登録できる")
    void should_allowSameUsername_when_emailsAreDifferent() {
        final CreateUserCommand first = createUserCommand();
        final CreateUserCommand second = CreateUserCommand.of(
                first.getUserName(),
                EmailAddress.of("other@example.com"),
                RawPassword.of("Another12"),
                first.getBirthDate()
        );
        when(passwordEncoder.encode(any(String.class))).thenReturn("encoded-password");
        final UserCommandService service = newService(sequenceUserIdGenerator(
        ));

        service.createUser(first);
        service.createUser(second);

        final ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, org.mockito.Mockito.times(2)).saveUser(captor.capture());
        final List<User> actual = captor.getAllValues();

        assertThat(actual).hasSize(2);
        assertThat(actual.getFirst().getUserName()).isEqualTo(first.getUserName());
        assertThat(actual.getLast().getUserName()).isEqualTo(second.getUserName());
        assertThat(actual.getFirst().getEmail()).isEqualTo(first.getEmail());
        assertThat(actual.getLast().getEmail()).isEqualTo(second.getEmail());
        assertThat(actual.getFirst().getUserId().getValue()).isEqualTo("01ARZ3NDEKTSV4RRFFQ69G5FAV");
        assertThat(actual.getLast().getUserId().getValue()).isEqualTo("01ARZ3NDEKTSV4RRFFQ69G5FAW");

        verify(userDomainService).ensureEmailNotAlreadyUsed(first.getEmail());
        verify(userDomainService).ensureEmailNotAlreadyUsed(second.getEmail());
    }

    @Test
    @DisplayName("UserCommandService.createUser はメール重複例外をそのまま送出する")
    void should_propagateException_when_emailAlreadyUsed() {
        final CreateUserCommand command = createUserCommand();
        final EmailAlreadyUsedException exception = new EmailAlreadyUsedException(command.getEmail());
        doThrow(exception).when(userDomainService).ensureEmailNotAlreadyUsed(command.getEmail());

        assertThatThrownBy(() -> newService().createUser(command))
                .isSameAs(exception);

        verifyNoInteractions(passwordEncoder, userRepository);
    }

    @Test
    @DisplayName("UserCommandService.createUser は保存失敗を UserCommandFailedException に変換する")
    void should_throwUsecaseException_when_saveFails() {
        final CreateUserCommand command = createUserCommand();
        when(passwordEncoder.encode(command.getRawPassword().getValue())).thenReturn("encoded-password");
        doThrow(new DataAccessResourceFailureException("db down")).when(userRepository).saveUser(any());

        assertThatThrownBy(() -> newService().createUser(command))
                .isInstanceOf(UserCommandFailedException.class)
                .satisfies(throwable -> {
                    final UserCommandFailedException exception = (UserCommandFailedException) throwable;
                    assertThat(exception.getDetails())
                            .containsEntry("target", "user")
                            .containsEntry("reason", "save_failed");
                });
    }

    private static CreateUserCommand createUserCommand() {
        return CreateUserCommand.of(
                UserName.of("user_01"),
                EmailAddress.of("user@example.com"),
                RawPassword.of("Secret12!"),
                BirthDate.restore(LocalDate.of(2000, 1, 1))
        );
    }

    private static UserIdGenerator fixedUserIdGenerator() {
        return () -> jp.i432kg.footprint.domain.value.UserId.of(UserCommandServiceTest.FIXED_USER_ID);
    }

    private static UserIdGenerator sequenceUserIdGenerator() {
        final List<String> sequence = List.of(new String[]{"01ARZ3NDEKTSV4RRFFQ69G5FAV", "01ARZ3NDEKTSV4RRFFQ69G5FAW"});
        final int[] index = {0};
        return () -> jp.i432kg.footprint.domain.value.UserId.of(sequence.get(index[0]++));
    }
}
