package jp.i432kg.footprint.presentation.api.response.mapper;

import jp.i432kg.footprint.application.query.model.ImageSummary;
import jp.i432kg.footprint.application.query.model.LocationSummary;
import jp.i432kg.footprint.application.query.model.PostSummary;
import jp.i432kg.footprint.domain.value.StorageObject;
import jp.i432kg.footprint.presentation.api.response.ImageResponse;
import jp.i432kg.footprint.presentation.api.response.LocationResponse;
import jp.i432kg.footprint.presentation.api.response.PostItemResponse;
import jp.i432kg.footprint.presentation.helper.ImageUrlConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 投稿のクエリモデルをレスポンス形式に変換するマッパー
 */
@Component
@RequiredArgsConstructor
public class PostResponseMapper {

    private final ImageUrlConverter imageUrlConverter;

    /**
     * {@link PostSummary} を {@link PostItemResponse} に変換します。
     *
     * @param summary 投稿の参照専用モデル
     * @return 投稿のアイテムレスポンス。引数が null の場合は null を返します。
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
     * {@link PostSummary} のリストを {@link PostItemResponse} のリストに変換します。
     *
     * @param summaries 投稿の参照専用モデルのリスト
     * @return 投稿のアイテムレスポンスリスト。引数が null の場合は空のリストを返します。
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
                            imageUrlConverter.convert(storageObject),
                            s.getContentType(),
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