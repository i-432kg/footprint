package jp.i432kg.footprint.presentation.api.response.mapper;

import jp.i432kg.footprint.application.query.model.ImageSummary;
import jp.i432kg.footprint.application.query.model.LocationSummary;
import jp.i432kg.footprint.application.query.model.PostSummary;
import jp.i432kg.footprint.domain.DomainTestFixtures;
import jp.i432kg.footprint.domain.value.StorageObject;
import jp.i432kg.footprint.presentation.api.response.PostItemResponse;
import jp.i432kg.footprint.presentation.helper.ImageUrlResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostResponseMapperTest {

    @Mock
    private ImageUrlResolver imageUrlResolver;

    @Test
    @DisplayName("PostResponseMapper は投稿サマリーの全項目をレスポンスへ変換する")
    void should_mapPostSummaryToResponse_when_summaryHasAllFields() {
        final ImageSummary imageSummary = imageSummary();
        final LocationSummary locationSummary = new LocationSummary(35.681236, 139.767125);
        final LocalDateTime createdAt = LocalDateTime.of(2026, 4, 18, 19, 15, 30);
        final PostSummary summary = new PostSummary(
                "post-01",
                "caption",
                List.of(imageSummary),
                true,
                locationSummary,
                createdAt
        );
        when(imageUrlResolver.resolve(StorageObject.of(imageSummary.getStorageType(), imageSummary.getObjectKey())))
                .thenReturn("https://example.com/images/post-01.jpg");

        final PostItemResponse actual = new PostResponseMapper(imageUrlResolver).from(summary);

        assertThat(actual.getId()).isEqualTo("post-01");
        assertThat(actual.getCaption()).isEqualTo("caption");
        assertThat(actual.isHasLocation()).isTrue();
        assertThat(actual.getLocation()).isNotNull();
        assertThat(actual.getLocation().getLat()).isEqualTo(35.681236);
        assertThat(actual.getLocation().getLng()).isEqualTo(139.767125);
        assertThat(actual.getCreatedAt()).isEqualTo(OffsetDateTime.of(createdAt, ZoneOffset.UTC));
        assertThat(actual.getImages()).singleElement().satisfies(image -> {
            assertThat(image.getId()).isEqualTo("image-01");
            assertThat(image.getSortOrder()).isEqualTo(1);
            assertThat(image.getUrl()).isEqualTo("https://example.com/images/post-01.jpg");
            assertThat(image.getFileExtension()).isEqualTo("jpg");
            assertThat(image.getSizeBytes()).isEqualTo(2048L);
            assertThat(image.getWidth()).isEqualTo(1920);
            assertThat(image.getHeight()).isEqualTo(1080);
        });
    }

    @Test
    @DisplayName("PostResponseMapper は画像 URL を ImageUrlResolver で解決する")
    void should_resolveImageUrl_when_mappingImageSummary() {
        final ImageSummary imageSummary = imageSummary();
        final PostSummary summary = new PostSummary(
                "post-01",
                "caption",
                List.of(imageSummary),
                false,
                null,
                LocalDateTime.of(2026, 4, 18, 19, 15, 30)
        );
        when(imageUrlResolver.resolve(StorageObject.of(imageSummary.getStorageType(), imageSummary.getObjectKey())))
                .thenReturn("https://example.com/images/post-01.jpg");

        new PostResponseMapper(imageUrlResolver).from(summary);

        final ArgumentCaptor<StorageObject> captor = ArgumentCaptor.forClass(StorageObject.class);
        verify(imageUrlResolver).resolve(captor.capture());
        assertThat(captor.getValue())
                .isEqualTo(StorageObject.of(imageSummary.getStorageType(), imageSummary.getObjectKey()));
    }

    @Test
    @DisplayName("PostResponseMapper は createdAt を UTC オフセット付き日時へ変換する")
    void should_convertCreatedAtToUtcOffsetDateTime_when_mappingPostSummary() {
        final LocalDateTime createdAt = LocalDateTime.of(2026, 4, 18, 19, 15, 30);
        final PostSummary summary = new PostSummary(
                "post-01",
                "caption",
                List.of(),
                false,
                null,
                createdAt
        );

        final PostItemResponse actual = new PostResponseMapper(imageUrlResolver).from(summary);

        assertThat(actual.getCreatedAt()).isEqualTo(OffsetDateTime.of(createdAt, ZoneOffset.UTC));
    }

    @Test
    @DisplayName("PostResponseMapper は位置情報なし投稿を null 項目入りの location オブジェクトとして変換する")
    void should_returnNullFieldLocationObject_when_postHasNoLocation() {
        final PostSummary summary = new PostSummary(
                "post-01",
                "caption",
                List.of(),
                false,
                null,
                LocalDateTime.of(2026, 4, 18, 19, 15, 30)
        );

        final PostItemResponse actual = new PostResponseMapper(imageUrlResolver).from(summary);

        assertThat(actual.isHasLocation()).isFalse();
        assertThat(actual.getLocation()).isNotNull();
        assertThat(actual.getLocation().getLat()).isNull();
        assertThat(actual.getLocation().getLng()).isNull();
    }

    @Test
    @DisplayName("PostResponseMapper は nullable な緯度経度をそのまま引き継ぐ")
    void should_preserveNullableCoordinates_when_locationSummaryHasNullValues() {
        final PostSummary summary = new PostSummary(
                "post-01",
                "caption",
                List.of(),
                true,
                new LocationSummary(null, null),
                LocalDateTime.of(2026, 4, 18, 19, 15, 30)
        );

        final PostItemResponse actual = new PostResponseMapper(imageUrlResolver).from(summary);

        assertThat(actual.getLocation()).isNotNull();
        assertThat(actual.getLocation().getLat()).isNull();
        assertThat(actual.getLocation().getLng()).isNull();
    }

    @Test
    @DisplayName("PostResponseMapper は投稿一覧を順序を保って変換する")
    void should_mapPostSummaryList_when_summariesArePresent() {
        final ImageSummary firstImageSummary = imageSummary();
        final ImageSummary secondImageSummary = new ImageSummary(
                "image-02",
                2,
                DomainTestFixtures.storageObject().getStorageType(),
                DomainTestFixtures.objectKey(),
                "jpg",
                1024L,
                1280,
                720
        );
        final PostSummary first = new PostSummary(
                "post-01",
                "first",
                List.of(firstImageSummary),
                false,
                null,
                LocalDateTime.of(2026, 4, 18, 19, 15, 30)
        );
        final PostSummary second = new PostSummary(
                "post-02",
                "second",
                List.of(secondImageSummary),
                false,
                null,
                LocalDateTime.of(2026, 4, 18, 20, 15, 30)
        );
        when(imageUrlResolver.resolve(StorageObject.of(firstImageSummary.getStorageType(), firstImageSummary.getObjectKey())))
                .thenReturn("https://example.com/images/post-01.jpg");
        when(imageUrlResolver.resolve(StorageObject.of(secondImageSummary.getStorageType(), secondImageSummary.getObjectKey())))
                .thenReturn("https://example.com/images/post-02.jpg");

        final List<PostItemResponse> actual = new PostResponseMapper(imageUrlResolver).fromList(List.of(first, second));

        assertThat(actual).extracting(PostItemResponse::getId).containsExactly("post-01", "post-02");
        verify(imageUrlResolver, times(2)).resolve(org.mockito.ArgumentMatchers.any(StorageObject.class));
    }

    private static ImageSummary imageSummary() {
        return new ImageSummary(
                "image-01",
                1,
                DomainTestFixtures.storageObject().getStorageType(),
                DomainTestFixtures.objectKey(),
                "jpg",
                2048L,
                1920,
                1080
        );
    }

}
