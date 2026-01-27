package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.value.*;
import lombok.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class Reply {

    @NonNull
    ReplyId id;

    @NonNull
    PostId postId;

    @NonNull
    UserId userId;

    @Nullable
    ReplyId parentReplyId;

    @NonNull
    Comment content;

    @NonNull
    LocalDateTime createdAt;

    @NonNull
    ReplyCount childCount;

    @Value
    @Builder(toBuilder = true)
    public static class NewReply {

        @NonNull
        PostId postId;

        @NonNull
        UserId userId;

        @Nullable
        ReplyId parentReplyId;

        @NonNull
        Comment content;

        public boolean hasParentReply(){
            return parentReplyId != null;
        }

    }

    public static NewReply.NewReplyBuilder newReply() {
        return NewReply.builder();
    }
}
