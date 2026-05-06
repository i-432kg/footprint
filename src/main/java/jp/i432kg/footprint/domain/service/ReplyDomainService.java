package jp.i432kg.footprint.domain.service;

import jp.i432kg.footprint.domain.exception.ReplyPostMismatchException;
import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.repository.ReplyRepository;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.ReplyId;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * 返信に関するドメインサービス
 */
@RequiredArgsConstructor
public class ReplyDomainService {

    private final ReplyRepository replyRepository;

    /**
     * 返信 ID で返信を取得します。
     */
    public Optional<Reply> findReplyById(final @Nullable ReplyId replyId) {
        if (replyId == null) {
            return Optional.empty();
        }
        return replyRepository.findReplyById(replyId);
    }

    /**
     * 親返信が指定された投稿に属しているかどうかを検証します。
     *
     * @param postId 返信先の投稿 ID
     * @param parentReply 親返信
     * @throws ReplyPostMismatchException 親返信が別の投稿に属する場合
     */
    public void validateParentReplyBelongsToPost(final PostId postId, final Reply parentReply)
            throws ReplyPostMismatchException {
        final PostId expectedPostId = parentReply.getPostId();
        if (!expectedPostId.equals(postId)) {
            throw new ReplyPostMismatchException(expectedPostId, postId);
        }
    }
}
