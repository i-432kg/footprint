package jp.i432kg.footprint.presentation.api.response.mapper;

import jp.i432kg.footprint.application.query.model.ReplySummary;
import jp.i432kg.footprint.presentation.api.response.ReplyItemResponse;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;

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
     * @param summary 返信 query model
     * @return 返信レスポンス
     */
    public ReplyItemResponse from(final ReplySummary summary) {
        return ReplyItemResponse.of(
                summary.getId(),
                summary.getPostId(),
                summary.getParentReplyId(),
                summary.getMessage(),
                summary.getChildCount(),
                summary.getCreatedAt().atOffset(ZoneOffset.UTC)
        );
    }

    /**
     * 返信 query model のリストを返信レスポンスのリストへ変換します。
     *
     * @param summaries 返信 query model のリスト
     * @return 変換後のレスポンス一覧
     */
    public List<ReplyItemResponse> fromList(final List<ReplySummary> summaries) {
        return summaries.stream()
                .map(this::from)
                .toList();
    }
}
