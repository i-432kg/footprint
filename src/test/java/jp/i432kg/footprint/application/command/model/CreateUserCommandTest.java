package jp.i432kg.footprint.application.command.model;

import jp.i432kg.footprint.domain.value.BirthDate;
import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.domain.value.RawPassword;
import jp.i432kg.footprint.domain.value.UserName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CreateUserCommandTest {

    @Test
    @DisplayName("CreateUserCommand.of は渡された値を保持する")
    void should_createCommand_when_valuesAreProvided() {
        final UserName userName = UserName.of("user_01");
        final EmailAddress email = EmailAddress.of("user@example.com");
        final RawPassword rawPassword = RawPassword.of("Secret12!");
        final BirthDate birthDate = BirthDate.of(LocalDate.of(2000, 1, 1));

        final CreateUserCommand actual = CreateUserCommand.of(
                userName,
                email,
                rawPassword,
                birthDate
        );

        assertThat(actual.getUserName()).isEqualTo(userName);
        assertThat(actual.getEmail()).isEqualTo(email);
        assertThat(actual.getRawPassword()).isEqualTo(rawPassword);
        assertThat(actual.getBirthDate()).isEqualTo(birthDate);
    }
}
