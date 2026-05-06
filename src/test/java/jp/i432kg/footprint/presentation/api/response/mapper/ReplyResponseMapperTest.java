package jp.i432kg.footprint.presentation.api.response.mapper;

import jp.i432kg.footprint.application.query.model.ReplySummary;
import jp.i432kg.footprint.presentation.api.response.ReplyItemResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReplyResponseMapperTest {

    private final ReplyResponseMapper mapper = new ReplyResponseMapper();

    @Test
    @DisplayName("ReplyResponseMapper は返信サマリーの全項目をレスポンスへ変換する")
    void should_mapReplySummaryToResponse_when_summaryHasAllFields() {
        final LocalDateTime createdAt = LocalDateTime.of(2026, 4, 18, 19, 15, 30);
        final ReplySummary summary = new ReplySummary(
                "reply-01",
                "post-01",
                "user-01",
                "reply-parent-01",
                "reply message",
                2,
                createdAt
        );

        final ReplyItemResponse actual = mapper.from(summary);

        assertThat(actual.getId()).isEqualTo("reply-01");
        assertThat(actual.getPostId()).isEqualTo("post-01");
        assertThat(actual.getParentReplyId()).isEqualTo("reply-parent-01");
        assertThat(actual.getMessage()).isEqualTo("reply message");
        assertThat(actual.getChildCount()).isEqualTo(2);
        assertThat(actual.getCreatedAt()).isEqualTo(OffsetDateTime.of(createdAt, ZoneOffset.UTC));
    }

    @Test
    @DisplayName("ReplyResponseMapper は createdAt を UTC オフセット付き日時へ変換する")
    void should_convertCreatedAtToUtcOffsetDateTime_when_mappingReplySummary() {
        final LocalDateTime createdAt = LocalDateTime.of(2026, 4, 18, 19, 15, 30);
        final ReplySummary summary = new ReplySummary(
                "reply-01",
                "post-01",
                "user-01",
                null,
                "reply message",
                0,
                createdAt
        );

        final ReplyItemResponse actual = mapper.from(summary);

        assertThat(actual.getCreatedAt()).isEqualTo(OffsetDateTime.of(createdAt, ZoneOffset.UTC));
    }

    @Test
    @DisplayName("ReplyResponseMapper はルート返信の null 親返信 ID をそのまま引き継ぐ")
    void should_allowNullParentReplyId_when_replyIsRoot() {
        final ReplySummary summary = new ReplySummary(
                "reply-01",
                "post-01",
                "user-01",
                null,
                "reply message",
                0,
                LocalDateTime.of(2026, 4, 18, 19, 15, 30)
        );

        final ReplyItemResponse actual = mapper.from(summary);

        assertThat(actual.getParentReplyId()).isNull();
    }

    @Test
    @DisplayName("ReplyResponseMapper は返信一覧を順序を保って変換する")
    void should_mapReplySummaryList_when_summariesArePresent() {
        final ReplySummary first = new ReplySummary(
                "reply-01",
                "post-01",
                "user-01",
                null,
                "first",
                0,
                LocalDateTime.of(2026, 4, 18, 19, 15, 30)
        );
        final ReplySummary second = new ReplySummary(
                "reply-02",
                "post-01",
                "user-01",
                "reply-01",
                "second",
                1,
                LocalDateTime.of(2026, 4, 18, 20, 15, 30)
        );

        final List<ReplyItemResponse> actual = mapper.fromList(List.of(first, second));

        assertThat(actual).extracting(ReplyItemResponse::getId).containsExactly("reply-01", "reply-02");
    }

}
