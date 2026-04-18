package jp.i432kg.footprint.presentation.api.response.mapper;

import jp.i432kg.footprint.application.query.model.ReplySummary;
import jp.i432kg.footprint.presentation.api.response.ReplyItemResponse;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 返信系 query model を API レスポンス DTO へ変換する mapper です。
 */
@Component
public class ReplyResponseMapper {

    /**
     * 返信 query model を返信レスポンスへ変換します。
     * <p>
     * 作成日時は UTC オフセット付きの値へ変換します。
     *
     * @param summary 返信 query model。{@code null} の場合は {@code null}
     * @return 返信レスポンス。引数が {@code null} の場合は {@code null}
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
     * 返信 query model のリストを返信レスポンスのリストへ変換します。
     *
     * @param summaries 返信 query model のリスト。{@code null} 可
     * @return 変換後のレスポンス一覧。引数が {@code null} の場合は空リスト
     */
    public List<ReplyItemResponse> fromList(final List<ReplySummary> summaries) {
        return Optional.ofNullable(summaries).orElseGet(List::of).stream()
                .filter(Objects::nonNull)
                .map(this::from)
                .toList();
    }
}
