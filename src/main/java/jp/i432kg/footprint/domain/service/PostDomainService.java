package jp.i432kg.footprint.domain.service;

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
     * 投稿作成が有効かどうか判定します。
     *
     * @param userId ユーザー ID
     * @return 投稿作成が有効な場合 true / 無効の場合 false
     * @throws Exception
     */
    public boolean isValidCreatePost(final UserId userId) throws Exception {

        // 投稿を行うユーザー IDが存在するか確認
        if (!userDomainService.isExistUser(userId)) {
            // TODO 独自例外にする
            throw new Exception();
        }

        return true;
    }
}
