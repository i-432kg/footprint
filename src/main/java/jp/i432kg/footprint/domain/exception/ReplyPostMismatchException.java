package jp.i432kg.footprint.domain.exception;

import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.exception.ErrorCode;

import java.util.Map;

/**
 * 親返信が期待した投稿に属していないことを表す業務例外です。
 * <p>
 * 返信作成時に、指定された親返信の投稿 ID と、
 * リクエスト対象の投稿 ID が一致しない場合に送出します。
 */
public class ReplyPostMismatchException extends DomainException {

    /**
     * 親返信と投稿の不整合を表す例外を生成します。
     *
     * @param expectedPostId 親返信が属しているべき投稿 ID
     * @param actualPostId リクエストで指定された投稿 ID
     */
    public ReplyPostMismatchException(final PostId expectedPostId, final PostId actualPostId) {
        super(
                ErrorCode.REPLY_POST_MISMATCH,
                "Reply post mismatch. expectedPostId=" + expectedPostId + ", actualPostId=" + actualPostId,
                details(
                        "reply.postId",
                        "post_mismatch",
                        Map.of(
                                "expectedPostId", expectedPostId.getValue(),
                                "actualPostId", actualPostId.getValue()
                        )
                )
        );
    }
}
