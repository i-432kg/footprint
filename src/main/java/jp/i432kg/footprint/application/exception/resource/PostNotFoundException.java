package jp.i432kg.footprint.application.exception.resource;

import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.exception.ErrorCode;

/**
 * 指定された投稿が存在しないことを表す例外です。
 */
public class PostNotFoundException extends ResourceNotFoundException {

    /**
     * 投稿未検出例外を生成します。
     *
     * @param postId 未検出となった投稿 ID
     */
    public PostNotFoundException(final PostId postId) {
        super(
                ErrorCode.POST_NOT_FOUND,
                "Post not found. postId=" + postId.getValue(),
                details("post", postId.getValue())
        );
    }
}
