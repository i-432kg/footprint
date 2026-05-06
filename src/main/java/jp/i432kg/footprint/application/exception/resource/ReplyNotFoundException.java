package jp.i432kg.footprint.application.exception.resource;

import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.exception.ErrorCode;

/**
 * 指定された返信が存在しないことを表す例外です。
 */
public class ReplyNotFoundException extends ResourceNotFoundException {

    /**
     * 返信未検出例外を生成します。
     *
     * @param replyId 未検出となった返信 ID
     */
    public ReplyNotFoundException(final ReplyId replyId) {
        super(
                ErrorCode.REPLY_NOT_FOUND,
                "Reply not found. replyId=" + replyId.getValue(),
                details("reply", replyId.getValue())
        );
    }
}
