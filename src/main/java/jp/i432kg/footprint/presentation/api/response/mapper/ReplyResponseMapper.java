package jp.i432kg.footprint.presentation.api.response.mapper;

import jp.i432kg.footprint.application.query.model.ReplySummary;
import jp.i432kg.footprint.presentation.api.response.ReplyItemResponse;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 返信のクエリモデルをレスポンス形式に変換するマッパー
 */
@Component
public class ReplyResponseMapper {

    /**
     * {@link ReplySummary} を {@link ReplyItemResponse} に変換します。
     * <p>
     * 作成日時は UTC オフセットに変換されます。
     *
     * @param summary 返信の参照専用モデル
     * @return 返信のアイテムレスポンス。引数が null の場合は null を返します。
     */
    public ReplyItemResponse from(final ReplySummary summary) {
        return Optional.ofNullable(summary)
                .map(s -> ReplyItemResponse.of(
                        s.getId(),
                        s.getPostId(),
                        s.getParentReplyId(),
                        s.getMessage(),
                        s.getChildCount(),
                        Objects.requireNonNull(s.getCreatedAt()).atOffset(ZoneOffset.UTC)
                ))
                .orElse(null);
    }

    /**
     * {@link ReplySummary} のリストを {@link ReplyItemResponse} のリストに変換します。
     *
     * @param summaries 返信の参照専用モデルのリスト
     * @return 返信のアイテムレスポンスリスト。引数が null の場合は空のリストを返します。
     */
    public List<ReplyItemResponse> fromList(final List<ReplySummary> summaries) {
        return Optional.ofNullable(summaries).orElseGet(List::of).stream()
                .filter(Objects::nonNull)
                .map(this::from)
                .toList();
    }
}