package jp.i432kg.footprint.presentation.api;

import jakarta.servlet.http.HttpServletRequest;
import jp.i432kg.footprint.application.command.model.CreateUserCommand;
import jp.i432kg.footprint.application.command.service.UserCommandService;
import jp.i432kg.footprint.application.query.model.PostSummary;
import jp.i432kg.footprint.application.query.model.ReplySummary;
import jp.i432kg.footprint.application.query.model.UserProfileSummary;
import jp.i432kg.footprint.application.query.service.PostQueryService;
import jp.i432kg.footprint.application.query.service.ReplyQueryService;
import jp.i432kg.footprint.application.query.service.UserQueryService;
import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.infrastructure.security.UserDetailsImpl;
import jp.i432kg.footprint.infrastructure.security.mapper.AuthMapper;
import jp.i432kg.footprint.presentation.api.request.SignUpRequest;
import jp.i432kg.footprint.presentation.api.response.PostItemResponse;
import jp.i432kg.footprint.presentation.api.response.ReplyItemResponse;
import jp.i432kg.footprint.presentation.api.response.UserProfileItemResponse;
import jp.i432kg.footprint.presentation.api.response.mapper.PostResponseMapper;
import jp.i432kg.footprint.presentation.api.response.mapper.ReplyResponseMapper;
import jp.i432kg.footprint.presentation.api.response.mapper.UserProfileResponseMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRestControllerTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-04-19T01:15:30Z"),
            ZoneId.of("Asia/Tokyo")
    );

    @Mock
    private PostQueryService postQueryService;

    @Mock
    private ReplyQueryService replyQueryService;

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private UserCommandService userCommandService;

    @Mock
    private PostResponseMapper postResponseMapper;

    @Mock
    private ReplyResponseMapper replyResponseMapper;

    @Mock
    private UserProfileResponseMapper userProfileResponseMapper;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Test
    @DisplayName("UserRestController は現在ユーザープロフィール取得時に mapper 結果を 200 で返す")
    void should_returnCurrentUserProfile_when_getCurrentUserIsCalled() {
        final UserProfileSummary summary = new UserProfileSummary();
        final UserProfileItemResponse response = UserProfileItemResponse.of("user-01", "user", "user@example.com", 3, 5);
        when(userQueryService.getUserProfile(DomainTestFixtures.userId())).thenReturn(summary);
        when(userProfileResponseMapper.from(summary)).thenReturn(response);

        final var actual = newController().getCurrentUser(userDetails());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isEqualTo(response);
    }

    @Test
    @DisplayName("UserRestController は自分の投稿一覧取得時に 200 を返す")
    void should_returnMyPosts_when_getMyPostsIsCalled() {
        final List<PostSummary> summaries = List.of(new PostSummary());
        final List<PostItemResponse> responses = List.of(
                PostItemResponse.of("post-01", "caption", List.of(), false, null, OffsetDateTime.now())
        );
        when(postQueryService.listMyPosts(DomainTestFixtures.userId(), DomainTestFixtures.postId(), 10)).thenReturn(summaries);
        when(postResponseMapper.fromList(summaries)).thenReturn(responses);

        final var actual = newController().getMyPosts(DomainTestFixtures.POST_ID, 10, userDetails());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isEqualTo(responses);
    }

    @Test
    @DisplayName("UserRestController は自分の返信一覧取得時に 200 を返す")
    void should_returnMyReplies_when_getMyRepliesIsCalled() {
        final List<ReplySummary> summaries = List.of(new ReplySummary());
        final List<ReplyItemResponse> responses = List.of(
                ReplyItemResponse.of("reply-01", "post-01", null, "reply", 0, OffsetDateTime.now())
        );
        when(replyQueryService.listMyReplies(DomainTestFixtures.userId(), DomainTestFixtures.replyId(), 10)).thenReturn(summaries);
        when(replyResponseMapper.fromList(summaries)).thenReturn(responses);

        final var actual = newController().getMyReplies(DomainTestFixtures.REPLY_ID, 10, userDetails());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isEqualTo(responses);
    }

    @Test
    @DisplayName("UserRestController はサインアップ情報から command を生成して 201 を返す")
    void should_createUser_when_createIsCalled() throws Exception {
        final SignUpRequest request = signUpRequest(LocalDate.of(2000, 1, 1));

        final var actual = newController().create(request, httpServletRequest);

        final ArgumentCaptor<CreateUserCommand> captor = ArgumentCaptor.forClass(CreateUserCommand.class);
        verify(userCommandService).createUser(captor.capture());
        final CreateUserCommand command = captor.getValue();

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(command.getUserName().getValue()).isEqualTo("user_name");
        assertThat(command.getEmail().getValue()).isEqualTo("user@example.com");
        assertThat(command.getRawPassword().getValue()).isEqualTo("Secret12");
        assertThat(command.getBirthDate().getValue()).isEqualTo(LocalDate.of(2000, 1, 1));
    }

    @Test
    @DisplayName("UserRestController は固定 Clock の当日を基準に BirthDate を生成する")
    void should_useFixedClockDate_when_creatingBirthDate() throws Exception {
        final SignUpRequest request = signUpRequest(LocalDate.of(2026, 4, 19));

        newController().create(request, httpServletRequest);

        final ArgumentCaptor<CreateUserCommand> captor = ArgumentCaptor.forClass(CreateUserCommand.class);
        verify(userCommandService).createUser(captor.capture());
        assertThat(captor.getValue().getBirthDate().getValue()).isEqualTo(LocalDate.of(2026, 4, 19));
    }

    @Test
    @DisplayName("UserRestController は固定 Clock 基準で未来日の birthDate を拒否する")
    void should_throwException_when_birthDateIsFutureAgainstFixedClock() {
        final SignUpRequest request = signUpRequest(LocalDate.of(2026, 4, 20));

        assertThatThrownBy(() -> newController().create(request, httpServletRequest))
                .isInstanceOf(jp.i432kg.footprint.domain.exception.InvalidValueException.class);
    }

    @Test
    @DisplayName("UserRestController はユーザー作成後に request.login を呼ぶ")
    void should_loginAfterUserCreation_when_createSucceeds() throws Exception {
        final SignUpRequest request = signUpRequest(LocalDate.of(2000, 1, 1));

        newController().create(request, httpServletRequest);

        verify(httpServletRequest).login("user@example.com", "Secret12");
    }

    private UserRestController newController() {
        return new UserRestController(
                postQueryService,
                replyQueryService,
                userQueryService,
                userCommandService,
                FIXED_CLOCK,
                postResponseMapper,
                replyResponseMapper,
                userProfileResponseMapper
        );
    }

    private static UserDetailsImpl userDetails() {
        return UserDetailsImpl.fromEntity(
                new AuthMapper.AuthUserEntity(
                        DomainTestFixtures.userId(),
                        "user@example.com",
                        "user",
                        "password"
                )
        );
    }

    private static SignUpRequest signUpRequest(final LocalDate birthDate) {
        final SignUpRequest request = new SignUpRequest();
        request.setUserName("user_name");
        request.setEmail("user@example.com");
        request.setPassword("Secret12");
        request.setBirthDate(birthDate);
        return request;
    }

}
