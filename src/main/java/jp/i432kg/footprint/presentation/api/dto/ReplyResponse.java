package jp.i432kg.footprint.presentation.api.dto;

import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.value.ReplyId;
import lombok.Value;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Optional;

@Value
public class ReplyResponse {

    @NonNull
    Integer id;

    @NonNull
    Integer postId;

    @Nullable
    Integer parentReplyId;

    @NonNull
    String content;

    @NonNull
    LocalDateTime createdAt;

    @NonNull
    Integer replyCount;

    public ReplyResponse(final Reply reply) {
        this.id = reply.getId().value();
        this.postId = reply.getPostId().value();
        this.parentReplyId = Optional.ofNullable(reply.getParentReplyId())
                .map(ReplyId::value)
                .orElse(null);
        this.content = reply.getContent().value();
        this.createdAt = reply.getCreatedAt();
        this.replyCount = reply.getChildCount().value();
    }
}
