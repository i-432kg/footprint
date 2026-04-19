package jp.i432kg.footprint.infrastructure.datasource.query;

import jp.i432kg.footprint.application.exception.resource.PostNotFoundException;
import jp.i432kg.footprint.application.query.model.ImageSummary;
import jp.i432kg.footprint.application.query.model.LocationSummary;
import jp.i432kg.footprint.application.query.model.PostSummary;
import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.value.Latitude;
import jp.i432kg.footprint.domain.value.Longitude;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.SearchKeyword;
import jp.i432kg.footprint.domain.value.StorageType;
import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.infrastructure.datasource.mapper.query.PostQueryMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostQueryServiceImplTest {

    @Mock
    private PostQueryMapper postQueryMapper;

    @Test
    @DisplayName("PostQueryServiceImpl.listRecentPosts は mapper から取得した最新投稿一覧を返す")
    void should_returnRecentPosts_when_listRecentPostsCalled() {
        final PostId lastId = DomainTestFixtures.postId();
        final List<PostSummary> expected = List.of(postSummary("post-01"));
        when(postQueryMapper.findRecentPosts(lastId, 10)).thenReturn(expected);

        final List<PostSummary> actual = newService().listRecentPosts(lastId, 10);

        assertThat(actual).isEqualTo(expected);
        verify(postQueryMapper).findRecentPosts(lastId, 10);
        verifyNoMoreInteractions(postQueryMapper);
    }

    @Test
    @DisplayName("PostQueryServiceImpl.listRecentPosts は lastId が null の場合もそのまま mapper へ委譲する")
    void should_delegateNullLastId_when_listRecentPostsCalledWithoutPagingCursor() {
        final List<PostSummary> expected = List.of(postSummary("post-01"));
        when(postQueryMapper.findRecentPosts(null, 10)).thenReturn(expected);

        final List<PostSummary> actual = newService().listRecentPosts(null, 10);

        assertThat(actual).isEqualTo(expected);
        verify(postQueryMapper).findRecentPosts(null, 10);
        verifyNoMoreInteractions(postQueryMapper);
    }

    @Test
    @DisplayName("PostQueryServiceImpl.listMyPosts は mapper から取得した自分の投稿一覧を返す")
    void should_returnMyPosts_when_listMyPostsCalled() {
        final UserId userId = DomainTestFixtures.userId();
        final PostId lastId = DomainTestFixtures.postId();
        final List<PostSummary> expected = List.of(postSummary("post-01"));
        when(postQueryMapper.findMyPosts(userId, lastId, 10)).thenReturn(expected);

        final List<PostSummary> actual = newService().listMyPosts(userId, lastId, 10);

        assertThat(actual).isEqualTo(expected);
        verify(postQueryMapper).findMyPosts(userId, lastId, 10);
        verifyNoMoreInteractions(postQueryMapper);
    }

    @Test
    @DisplayName("PostQueryServiceImpl.searchPosts はキーワード検索結果を返す")
    void should_returnSearchedPosts_when_searchPostsCalled() {
        final SearchKeyword keyword = SearchKeyword.of("hello");
        final PostId lastId = DomainTestFixtures.postId();
        final List<PostSummary> expected = List.of(postSummary("post-01"));
        when(postQueryMapper.findPostsByKeyword(keyword, lastId, 10)).thenReturn(expected);

        final List<PostSummary> actual = newService().searchPosts(keyword, lastId, 10);

        assertThat(actual).isEqualTo(expected);
        verify(postQueryMapper).findPostsByKeyword(keyword, lastId, 10);
        verifyNoMoreInteractions(postQueryMapper);
    }

    @Test
    @DisplayName("PostQueryServiceImpl.searchPostsByBBox は境界ボックス検索結果を返す")
    void should_returnPostsInBoundingBox_when_searchPostsByBBoxCalled() {
        final Latitude minLat = DomainTestFixtures.latitude();
        final Latitude maxLat = Latitude.of(new BigDecimal("35.700000"));
        final Longitude minLng = DomainTestFixtures.longitude();
        final Longitude maxLng = Longitude.of(new BigDecimal("139.800000"));
        final List<PostSummary> expected = List.of(postSummary("post-01"));
        when(postQueryMapper.findPostsByBBox(minLat, maxLat, minLng, maxLng)).thenReturn(expected);

        final List<PostSummary> actual = newService().searchPostsByBBox(minLat, maxLat, minLng, maxLng);

        assertThat(actual).isEqualTo(expected);
        verify(postQueryMapper).findPostsByBBox(minLat, maxLat, minLng, maxLng);
        verifyNoMoreInteractions(postQueryMapper);
    }

    @Test
    @DisplayName("PostQueryServiceImpl.getPost は投稿が存在する場合に投稿詳細を返す")
    void should_returnPostSummary_when_getPostFindsPost() {
        final PostId postId = DomainTestFixtures.postId();
        final PostSummary expected = postSummary("post-01");
        when(postQueryMapper.findPostById(postId)).thenReturn(Optional.of(expected));

        final PostSummary actual = newService().getPost(postId);

        assertThat(actual).isEqualTo(expected);
        verify(postQueryMapper).findPostById(postId);
        verifyNoMoreInteractions(postQueryMapper);
    }

    @Test
    @DisplayName("PostQueryServiceImpl.getPost は投稿が存在しない場合に PostNotFoundException を送出する")
    void should_throwPostNotFoundException_when_getPostFindsNothing() {
        final PostId postId = DomainTestFixtures.postId();
        when(postQueryMapper.findPostById(postId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> newService().getPost(postId))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("Post not found. postId=" + postId.getValue());

        verify(postQueryMapper).findPostById(postId);
        verifyNoMoreInteractions(postQueryMapper);
    }

    @Test
    @DisplayName("PostQueryServiceImpl.findPost は mapper の Optional 結果をそのまま返す")
    void should_returnOptionalResult_when_findPostCalled() {
        final PostId postId = DomainTestFixtures.postId();
        final Optional<PostSummary> expected = Optional.of(postSummary("post-01"));
        when(postQueryMapper.findPostById(postId)).thenReturn(expected);

        final Optional<PostSummary> actual = newService().findPost(postId);

        assertThat(actual).isEqualTo(expected);
        verify(postQueryMapper).findPostById(postId);
        verifyNoMoreInteractions(postQueryMapper);
    }

    @Test
    @DisplayName("PostQueryServiceImpl.findPost は mapper が空を返した場合に Optional.empty をそのまま返す")
    void should_returnEmptyOptional_when_findPostReturnsNothing() {
        final PostId postId = DomainTestFixtures.postId();
        when(postQueryMapper.findPostById(postId)).thenReturn(Optional.empty());

        final Optional<PostSummary> actual = newService().findPost(postId);

        assertThat(actual).isEmpty();
        verify(postQueryMapper).findPostById(postId);
        verifyNoMoreInteractions(postQueryMapper);
    }

    private PostQueryServiceImpl newService() {
        return new PostQueryServiceImpl(postQueryMapper);
    }

    private static PostSummary postSummary(final String id) {
        return new PostSummary(
                id,
                "caption",
                List.of(new ImageSummary(
                        "image-01",
                        0,
                        StorageType.LOCAL,
                        DomainTestFixtures.objectKey(),
                        DomainTestFixtures.fileExtension().getValue(),
                        1024L,
                        640,
                        480
                )),
                true,
                new LocationSummary(35.681236, 139.767125),
                LocalDateTime.of(2026, 4, 19, 12, 0)
        );
    }
}
