package jp.i432kg.footprint.domain.service;

import jp.i432kg.footprint.domain.repository.PostRepository;
import jp.i432kg.footprint.domain.value.PostId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 返信に関するドメインサービス
 */
@Service
@RequiredArgsConstructor
public class PostDomainService {

    private final PostRepository postRepository;

    /**
     * 対象の投稿が存在するかどうか判定します。
     *
     * @param postId 投稿 ID
     * @return 存在する場合は true / 存在しない場合は false
     */
    public boolean isExistPost(final PostId postId) {
        return postRepository.existsById(postId);
    }
}
