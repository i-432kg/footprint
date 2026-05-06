package jp.i432kg.footprint.presentation.api.response.mapper;

import jp.i432kg.footprint.application.query.model.ImageSummary;
import jp.i432kg.footprint.application.query.model.LocationSummary;
import jp.i432kg.footprint.application.query.model.PostSummary;
import jp.i432kg.footprint.domain.value.StorageObject;
import jp.i432kg.footprint.presentation.api.response.ImageResponse;
import jp.i432kg.footprint.presentation.api.response.LocationResponse;
import jp.i432kg.footprint.presentation.api.response.PostItemResponse;
import jp.i432kg.footprint.presentation.helper.ImageUrlResolver;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

/**
 * 投稿系 query model を API レスポンス DTO へ変換する mapper です。
 */
@Component
@RequiredArgsConstructor
public class PostResponseMapper {

    private final ImageUrlResolver imageUrlResolver;

    /**
     * 投稿 query model を投稿レスポンスへ変換します。
     * <p>
     * 作成日時は UTC オフセット付きの値へ変換し、画像 URL は {@link ImageUrlResolver} で解決します。
     *
     * @param summary 投稿 query model
     * @return 投稿レスポンス
     */
    public PostItemResponse from(final PostSummary summary) {
        return PostItemResponse.of(
                summary.getId(),
                summary.getCaption(),
                summary.getImages().stream().map(this::fromImageSummary).toList(),
                summary.isHasLocation(),
                this.fromLocationSummary(summary.getLocation()),
                summary.getCreatedAt().atOffset(ZoneOffset.UTC)
        );
    }

    /**
     * 投稿 query model のリストを投稿レスポンスのリストへ変換します。
     *
     * @param summaries 投稿 query model のリスト
     * @return 変換後のレスポンス一覧
     */
    public List<PostItemResponse> fromList(final List<PostSummary> summaries) {
        return summaries.stream()
                .map(this::from)
                .toList();
    }

    /**
     * 画像の参照モデルを画像レスポンスに変換します。
     *
     * @param summary 画像の参照モデル
     * @return 画像レスポンス
     */
    private ImageResponse fromImageSummary(final ImageSummary summary) {
        final StorageObject storageObject =
                StorageObject.of(summary.getStorageType(), summary.getObjectKey());

        return ImageResponse.of(
                summary.getId(),
                summary.getSortOrder(),
                imageUrlResolver.resolve(storageObject),
                summary.getFileExtension(),
                summary.getSizeBytes(),
                summary.getWidth(),
                summary.getHeight()
        );
    }

    /**
     * 位置情報の参照モデルを位置情報レスポンスに変換します。
     *
     * @param summary 位置情報の参照モデル。{@code null} の場合は各項目が {@code null} のレスポンスを返します
     * @return 位置情報レスポンス
     */
    private LocationResponse fromLocationSummary(final @Nullable LocationSummary summary) {

        return Objects.isNull(summary) ?
                LocationResponse.of(null, null) :
                LocationResponse.of(summary.getLat(), summary.getLng());
    }
}
