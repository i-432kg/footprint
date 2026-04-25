package jp.i432kg.footprint.domain.exception;

import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.exception.ErrorCode;

import java.util.Map;

public class ReplyPostMismatchException extends DomainException {

    public ReplyPostMismatchException(PostId expectedPostId, PostId actualPostId) {
        super(
                ErrorCode.REPLY_POST_MISMATCH,
                "Reply post mismatch. expectedPostId=" + expectedPostId + ", actualPostId=" + actualPostId,
                details(
                        "reply.postId",
                        "post_mismatch",
                        null,
                        Map.of(
                                "expectedPostId", expectedPostId.getValue(),
                                "actualPostId", actualPostId.getValue()
                        )
                )
        );
    }
}
