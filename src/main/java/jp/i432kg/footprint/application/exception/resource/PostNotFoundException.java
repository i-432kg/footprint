package jp.i432kg.footprint.application.exception.resource;

import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.exception.ErrorCode;

public class PostNotFoundException extends ResourceNotFoundException {

    public PostNotFoundException(PostId postId) {
        super(
                ErrorCode.POST_NOT_FOUND,
                "Post not found. postId=" + postId.getValue(),
                details("post", postId.getValue())
        );
    }
}
