package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.value.BirthDate;
import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.domain.value.HashedPassword;
import jp.i432kg.footprint.domain.value.UserName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    @DisplayName("User.of は妥当な値からユーザーを生成できる")
    void should_createUser_when_valuesAreValid() {
        final User actual = User.of(
                DomainTestFixtures.userId(),
                UserName.of("user_01"),
                EmailAddress.of("user@example.com"),
                HashedPassword.of("hashed-password"),
                BirthDate.restore(LocalDate.of(2000, 1, 1))
        );

        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("User.of は入力した値を保持する")
    void should_keepGivenValues_when_userIsCreated() {
        final User actual = User.of(
                DomainTestFixtures.userId(),
                UserName.of("user_01"),
                EmailAddress.of("user@example.com"),
                HashedPassword.of("hashed-password"),
                BirthDate.restore(LocalDate.of(2000, 1, 1))
        );

        assertThat(actual.getUserId()).isEqualTo(DomainTestFixtures.userId());
        assertThat(actual.getUserName()).isEqualTo(UserName.of("user_01"));
        assertThat(actual.getEmail()).isEqualTo(EmailAddress.of("user@example.com"));
        assertThat(actual.getHashedPassword()).isEqualTo(HashedPassword.of("hashed-password"));
        assertThat(actual.getBirthDate()).isEqualTo(BirthDate.restore(LocalDate.of(2000, 1, 1)));
    }
}
