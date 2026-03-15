package jp.i432kg.footprint.domain.service;

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
     * 返信が正当な状態であるか検証します。
     *
     * @param postId        返信先の投稿 ID
     * @param userId        返信者のユーザー ID
     * @param parentReplyId 親返信のID（存在する場合のみ）
     * @return すべての条件を満たす場合は true
     */
    public boolean isValidCreateReply(final PostId postId, final UserId userId, @Nullable final ReplyId parentReplyId)
            throws Exception {

        // 返信先の投稿が存在するか確認
        if (!postDomainService.isExistPost(postId)) {
            // TODO 独自例外にする
            throw new Exception();
        }

        // 返信者が存在するか確認（有効なユーザーか）
        if (!userDomainService.isExistUser(userId)) {
            // TODO 独自例外にする
            throw new Exception();
        }

        // 親返信がある場合、それが同じ投稿に属しているか確認
        if (Objects.nonNull(parentReplyId)) {
            final Optional<Reply> parentReply = replyRepository.findReplyById(parentReplyId);

            // 返信先の返信が存在するか確認
            if (parentReply.isEmpty()) {
                // TODO 独自例外にする
                throw new Exception();
            }

            // 返信先の返信が同一の投稿であるか確認
            if (!parentReply.get().getPostId().equals(postId)) {
                // TODO 独自例外にする
                throw new Exception();
            }
        }

        return true;
    }
}

