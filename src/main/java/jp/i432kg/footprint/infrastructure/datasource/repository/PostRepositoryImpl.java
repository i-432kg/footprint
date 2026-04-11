package jp.i432kg.footprint.infrastructure.datasource.repository;

import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.repository.PostRepository;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.infrastructure.datasource.mapper.repository.PostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * 投稿に関するリポジトリの実装クラス
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final PostMapper postMapper;

    @Override
    public boolean existsById(PostId postId) {
        try {
            return postMapper.countByPostId(postId) > 0;
        } catch (RuntimeException e) {
            log.error("Failed to check post existence. postId={}", postId.getValue(), e);
            throw e;
        }
    }

    @Override
    public void savePost(final Post post) {
        try {
            // 投稿レコードを保存する
            final PostMapper.PostInsertEntity postEntity = PostMapper.PostInsertEntity.from(post);
            postMapper.insertPosts(postEntity); // 呼び出し後、entity.id に採番された値がセットされる

            // 画像情報の保存
            final PostMapper.PostImageInsertEntity postImageEntity =
                    PostMapper.PostImageInsertEntity.from(post);
            postMapper.insertPostImages(postImageEntity);
        } catch (RuntimeException e) {
            log.error(
                    "Failed to save post. postId={}, userId={}",
                    post.getPostId().getValue(),
                    post.getUserId().getValue(),
                    e
            );
            throw e;
        }
    }
}
