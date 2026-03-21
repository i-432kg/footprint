package jp.i432kg.footprint.domain.service;

import jp.i432kg.footprint.application.exception.resource.UserNotFoundException;
import jp.i432kg.footprint.domain.repository.PostRepository;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 返信に関するドメインサービス
 */
@Service
@RequiredArgsConstructor
public class PostDomainService {

    private final PostRepository postRepository;

    private final UserDomainService userDomainService;

    /**
     * 対象の投稿が存在するかどうか判定します。
     *
     * @param postId 投稿 ID
     * @return 存在する場合は true / 存在しない場合は false
     */
    public boolean isExistPost(final PostId postId) {
        return postRepository.existsById(postId);
    }

    /**
     * 投稿作成時のバリデーションを行います。
     *
     * @param userId ユーザー ID
     * @throws UserNotFoundException 投稿を行うユーザーが存在しなかった場合の例外
     */
    public void validateCreatePost(final UserId userId) throws UserNotFoundException {

        // 投稿を行うユーザー IDが存在するか確認
        if (!userDomainService.isExistUser(userId)) {
            throw new UserNotFoundException(userId);
        }
    }
}
