package jp.i432kg.footprint.infrastructure.datasource.query;

import jp.i432kg.footprint.application.exception.resource.UserNotFoundException;
import jp.i432kg.footprint.application.query.model.UserProfileSummary;
import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.infrastructure.datasource.mapper.query.UserQueryMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceImplTest {

    @Mock
    private UserQueryMapper userQueryMapper;

    @Test
    @DisplayName("UserQueryServiceImpl.getUserProfile はユーザーが存在する場合にプロフィールを返す")
    void should_returnUserProfile_when_getUserProfileFindsUser() {
        final UserId userId = DomainTestFixtures.userId();
        final UserProfileSummary expected = userProfileSummary();
        when(userQueryMapper.findProfileByUserId(userId)).thenReturn(Optional.of(expected));

        final UserProfileSummary actual = newService().getUserProfile(userId);

        assertThat(actual).isEqualTo(expected);
        verify(userQueryMapper).findProfileByUserId(userId);
        verifyNoMoreInteractions(userQueryMapper);
    }

    @Test
    @DisplayName("UserQueryServiceImpl.getUserProfile はユーザーが存在しない場合に UserNotFoundException を送出する")
    void should_throwUserNotFoundException_when_getUserProfileFindsNothing() {
        final UserId userId = DomainTestFixtures.userId();
        when(userQueryMapper.findProfileByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> newService().getUserProfile(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found. userId=" + userId.getValue());

        verify(userQueryMapper).findProfileByUserId(userId);
        verifyNoMoreInteractions(userQueryMapper);
    }

    @Test
    @DisplayName("UserQueryServiceImpl.findUserProfile は mapper の Optional 結果をそのまま返す")
    void should_returnOptionalResult_when_findUserProfileCalled() {
        final UserId userId = DomainTestFixtures.userId();
        final Optional<UserProfileSummary> expected = Optional.of(userProfileSummary());
        when(userQueryMapper.findProfileByUserId(userId)).thenReturn(expected);

        final Optional<UserProfileSummary> actual = newService().findUserProfile(userId);

        assertThat(actual).isEqualTo(expected);
        verify(userQueryMapper).findProfileByUserId(userId);
        verifyNoMoreInteractions(userQueryMapper);
    }

    @Test
    @DisplayName("UserQueryServiceImpl.findUserProfile は mapper が空を返した場合に Optional.empty をそのまま返す")
    void should_returnEmptyOptional_when_findUserProfileReturnsNothing() {
        final UserId userId = DomainTestFixtures.userId();
        when(userQueryMapper.findProfileByUserId(userId)).thenReturn(Optional.empty());

        final Optional<UserProfileSummary> actual = newService().findUserProfile(userId);

        assertThat(actual).isEmpty();
        verify(userQueryMapper).findProfileByUserId(userId);
        verifyNoMoreInteractions(userQueryMapper);
    }

    private UserQueryServiceImpl newService() {
        return new UserQueryServiceImpl(userQueryMapper);
    }

    private static UserProfileSummary userProfileSummary() {
        return new UserProfileSummary(
                DomainTestFixtures.USER_ID,
                "user_01",
                "user@example.com",
                3,
                5
        );
    }
}
