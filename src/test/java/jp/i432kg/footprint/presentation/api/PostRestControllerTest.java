package jp.i432kg.footprint.presentation.api;

import jp.i432kg.footprint.application.command.model.CreatePostCommand;
import jp.i432kg.footprint.application.command.service.PostCommandService;
import jp.i432kg.footprint.application.query.model.PostSummary;
import jp.i432kg.footprint.application.query.model.ReplySummary;
import jp.i432kg.footprint.application.query.service.PostQueryService;
import jp.i432kg.footprint.application.query.service.ReplyQueryService;
import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.infrastructure.security.UserDetailsImpl;
import jp.i432kg.footprint.infrastructure.security.mapper.AuthMapper;
import jp.i432kg.footprint.presentation.api.request.PostRequest;
import jp.i432kg.footprint.presentation.api.response.PostItemResponse;
import jp.i432kg.footprint.presentation.api.response.ReplyItemResponse;
import jp.i432kg.footprint.presentation.api.response.mapper.PostResponseMapper;
import jp.i432kg.footprint.presentation.api.response.mapper.ReplyResponseMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostRestControllerTest {

    @Mock
    private PostCommandService postCommandService;

    @Mock
    private PostQueryService postQueryService;

    @Mock
    private ReplyQueryService replyQueryService;

    @Mock
    private PostResponseMapper postResponseMapper;

    @Mock
    private ReplyResponseMapper replyResponseMapper;

    @Test
    @DisplayName("PostRestController は最新投稿一覧取得時に service と mapper の結果を 200 で返す")
    void should_returnRecentPosts_when_getRecentPostsIsCalled() {
        final List<PostSummary> summaries = List.of(new PostSummary());
        final List<PostItemResponse> responses = List.of(dummyPostResponse());
        when(postQueryService.listRecentPosts(PostId.of("01ARZ3NDEKTSV4RRFFQ69G5FAX"), 10)).thenReturn(summaries);
        when(postResponseMapper.fromList(summaries)).thenReturn(responses);

        final var actual = newController().getRecentPosts("01ARZ3NDEKTSV4RRFFQ69G5FAX", 10);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isEqualTo(responses);
    }

    @Test
    @DisplayName("PostRestController は検索条件を値オブジェクトへ変換して投稿検索を行う")
    void should_searchPosts_when_searchIsCalled() {
        final List<PostSummary> summaries = List.of(new PostSummary());
        final List<PostItemResponse> responses = List.of(dummyPostResponse());
        when(postQueryService.searchPosts(
                org.mockito.ArgumentMatchers.eq(jp.i432kg.footprint.domain.value.SearchKeyword.of("hello")),
                org.mockito.ArgumentMatchers.eq(PostId.of("01ARZ3NDEKTSV4RRFFQ69G5FAX")),
                org.mockito.ArgumentMatchers.eq(10)
        )).thenReturn(summaries);
        when(postResponseMapper.fromList(summaries)).thenReturn(responses);

        final var actual = newController().search("hello", "01ARZ3NDEKTSV4RRFFQ69G5FAX", 10);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isEqualTo(responses);
    }

    @Test
    @DisplayName("PostRestController は地図範囲を値オブジェクトへ変換して投稿検索を行う")
    void should_searchPostsByMapBounds_when_searchMapIsCalled() {
        final List<PostSummary> summaries = List.of(new PostSummary());
        final List<PostItemResponse> responses = List.of(dummyPostResponse());
        when(postQueryService.searchPostsByBBox(
                org.mockito.ArgumentMatchers.eq(DomainTestFixtures.latitude()),
                org.mockito.ArgumentMatchers.eq(jp.i432kg.footprint.domain.value.Latitude.of(new java.math.BigDecimal("35.700000"))),
                org.mockito.ArgumentMatchers.eq(DomainTestFixtures.longitude()),
                org.mockito.ArgumentMatchers.eq(jp.i432kg.footprint.domain.value.Longitude.of(new java.math.BigDecimal("139.800000")))
        )).thenReturn(summaries);
        when(postResponseMapper.fromList(summaries)).thenReturn(responses);

        final var actual = newController().searchMap(
                new java.math.BigDecimal("35.681236"),
                new java.math.BigDecimal("35.700000"),
                new java.math.BigDecimal("139.767125"),
                new java.math.BigDecimal("139.800000")
        );

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isEqualTo(responses);
    }

    @Test
    @DisplayName("PostRestController は投稿詳細取得時に mapper 結果を 200 で返す")
    void should_returnPostDetail_when_getPostIsCalled() {
        final PostSummary summary = new PostSummary();
        final PostItemResponse response = dummyPostResponse();
        when(postQueryService.getPost(PostId.of("01ARZ3NDEKTSV4RRFFQ69G5FAX"))).thenReturn(summary);
        when(postResponseMapper.from(summary)).thenReturn(response);

        final var actual = newController().getPost("01ARZ3NDEKTSV4RRFFQ69G5FAX");

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isEqualTo(response);
    }

    @Test
    @DisplayName("PostRestController は投稿配下のトップレベル返信一覧を 200 で返す")
    void should_returnTopLevelReplies_when_getRepliesIsCalled() {
        final List<ReplySummary> summaries = List.of(new ReplySummary());
        final List<ReplyItemResponse> responses = List.of(dummyReplyResponse());
        when(replyQueryService.listTopLevelReplies(PostId.of("01ARZ3NDEKTSV4RRFFQ69G5FAX"))).thenReturn(summaries);
        when(replyResponseMapper.fromList(summaries)).thenReturn(responses);

        final var actual = newController().getReplies("01ARZ3NDEKTSV4RRFFQ69G5FAX");

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isEqualTo(responses);
    }

    @Test
    @DisplayName("PostRestController は投稿作成リクエストから command を生成して 201 を返す")
    void should_createPost_when_createIsCalled() throws IOException {
        final MockMultipartFile file =
                new MockMultipartFile("imageFile", "post.jpg", "image/jpeg", new byte[] {1, 2, 3});
        final PostRequest request = new PostRequest();
        request.setImageFile(file);
        request.setComment("caption");

        final var actual = newController().create(request, userDetails());

        final ArgumentCaptor<CreatePostCommand> captor = ArgumentCaptor.forClass(CreatePostCommand.class);
        verify(postCommandService).createPost(captor.capture());
        final CreatePostCommand command = captor.getValue();

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(command.getUserId()).isEqualTo(DomainTestFixtures.userId());
        assertThat(command.getComment().getValue()).isEqualTo("caption");
        assertThat(command.getOriginalFilename().getValue()).isEqualTo("post.jpg");
        assertThat(command.getImageStream()).isNotNull();
    }

    @Test
    @DisplayName("PostRestController は comment 未指定時に空文字コメントで command を生成する")
    void should_createPostWithEmptyComment_when_requestCommentIsNull() throws IOException {
        final MockMultipartFile file =
                new MockMultipartFile("imageFile", "post.jpg", "image/jpeg", new byte[] {1});
        final PostRequest request = new PostRequest();
        request.setImageFile(file);
        request.setComment(null);

        newController().create(request, userDetails());

        final ArgumentCaptor<CreatePostCommand> captor = ArgumentCaptor.forClass(CreatePostCommand.class);
        verify(postCommandService).createPost(captor.capture());
        assertThat(captor.getValue().getComment().getValue()).isEmpty();
    }

    private PostRestController newController() {
        return new PostRestController(
                postCommandService,
                postQueryService,
                replyQueryService,
                postResponseMapper,
                replyResponseMapper
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

    private static PostItemResponse dummyPostResponse() {
        return PostItemResponse.of("post-01", "caption", List.of(), false, null, java.time.OffsetDateTime.now());
    }

    private static ReplyItemResponse dummyReplyResponse() {
        return ReplyItemResponse.of("reply-01", "post-01", null, "reply", 0, java.time.OffsetDateTime.now());
    }

}
