package jp.i432kg.footprint.domain.service;

import jp.i432kg.footprint.application.exception.resource.PostNotFoundException;
import jp.i432kg.footprint.application.exception.resource.ReplyNotFoundException;
import jp.i432kg.footprint.application.exception.resource.UserNotFoundException;
import jp.i432kg.footprint.domain.exception.ReplyPostMismatchException;
import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.repository.ReplyRepository;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * 返信に関するドメインサービス
 */
@Service
@RequiredArgsConstructor
public class ReplyDomainService {

    private final ReplyRepository replyRepository;

    private final PostDomainService postDomainService;

    private final UserDomainService userDomainService;

    /**
     * 返信作成時のバリデーションを行います。
     *
     * @param postId        返信先の投稿 ID
     * @param userId        返信者のユーザー ID
     * @param parentReplyId 親となる返信の返信ID（存在する場合のみ）
     * @throws PostNotFoundException      返信対象の投稿が存在しなかった場合の例外
     * @throws UserNotFoundException      返信を行うユーザーが存在しなかった場合の例外
     * @throws ReplyNotFoundException     返信対象の返信が存在しなかった場合の例外
     * @throws ReplyPostMismatchException 返信対象の返信と元の投稿が一致しなかった場合の例外
     */
    public void validateCreateReply(final PostId postId, final UserId userId, @Nullable final ReplyId parentReplyId)
            throws PostNotFoundException, UserNotFoundException, ReplyNotFoundException, ReplyPostMismatchException {

        // 返信先の投稿が存在するか確認
        if (!postDomainService.isExistPost(postId)) {
            throw new PostNotFoundException(postId);
        }

        // 返信者が存在するか確認（有効なユーザーか）
        if (!userDomainService.isExistUser(userId)) {
            throw new UserNotFoundException(userId);
        }

        // 親返信がある場合、それが同じ投稿に属しているか確認
        if (Objects.nonNull(parentReplyId)) {
            final Optional<Reply> parentReply = replyRepository.findReplyById(parentReplyId);

            // 返信先の返信が存在するか確認
            if (parentReply.isEmpty()) {
                throw new ReplyNotFoundException(parentReplyId);
            }

            // 返信先の返信が同一の投稿であるか確認
            final PostId expectedPostId = parentReply.get().getPostId();
            if (!expectedPostId.equals(postId)) {
                throw new ReplyPostMismatchException(expectedPostId, postId);
            }
        }
    }
}

