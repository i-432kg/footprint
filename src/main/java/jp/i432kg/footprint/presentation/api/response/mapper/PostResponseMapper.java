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
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
     * @param summary 投稿 query model。{@code null} の場合は {@code null}
     * @return 投稿レスポンス。引数が {@code null} の場合は {@code null}
     */
    public PostItemResponse from(final PostSummary summary) {
        return Optional.ofNullable(summary)
                .map(s -> PostItemResponse.of(
                        s.getId(),
                        s.getCaption(),
                        Objects.requireNonNull(s.getImages()).stream().map(this::fromImageSummary).toList(),
                        s.isHasLocation(),
                        this.fromLocationSummary(s.getLocation()),
                        Objects.requireNonNull(s.getCreatedAt()).atOffset(ZoneOffset.UTC)
                ))
                .orElse(null);
    }

    /**
     * 投稿 query model のリストを投稿レスポンスのリストへ変換します。
     *
     * @param summaries 投稿 query model のリスト。{@code null} 可
     * @return 変換後のレスポンス一覧。引数が {@code null} の場合は空リスト
     */
    public List<PostItemResponse> fromList(final List<PostSummary> summaries) {
        return Optional.ofNullable(summaries).orElseGet(List::of).stream()
                .filter(Objects::nonNull)
                .map(this::from)
                .toList();
    }

    /**
     * 画像の参照モデルを画像レスポンスに変換します。
     *
     * @param summary 画像の参照モデル
     * @return 画像レスポンス。引数が null の場合は null を返します。
     */
    private ImageResponse fromImageSummary(final ImageSummary summary) {
        return Optional.ofNullable(summary)
                .map(s -> {
                    final StorageObject storageObject =
                            StorageObject.of(s.getStorageType(), s.getObjectKey());

                    return ImageResponse.of(
                            s.getId(),
                            s.getSortOrder(),
                            imageUrlResolver.resolve(storageObject),
                            s.getFileExtension(),
                            s.getSizeBytes(),
                            s.getWidth(),
                            s.getHeight()
                    );
                })
                .orElse(null);
    }

    /**
     * 位置情報の参照モデルを位置情報レスポンスに変換します。
     *
     * @param summary 位置情報の参照モデル
     * @return 位置情報レスポンス。引数が null の場合は null を返します。
     */
    private LocationResponse fromLocationSummary(final LocationSummary summary) {
        return Optional.ofNullable(summary)
                .map(s -> LocationResponse.of(s.getLat(), s.getLng()))
                .orElse(null);
    }
}
