package jp.i432kg.footprint.application.exception.resource;

import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.exception.ErrorCode;

import java.util.Map;

public class ReplyNotFoundException extends ResourceNotFoundException {

    public ReplyNotFoundException(ReplyId replyId) {
        super(
                ErrorCode.REPLY_NOT_FOUND,
                "Reply not found. replyId=" + replyId.value(),
                Map.of("replyId", replyId.value())
        );
    }
}