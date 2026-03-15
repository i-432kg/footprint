package jp.i432kg.footprint.infrastructure.datasource.repository;

import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.repository.PostRepository;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.infrastructure.datasource.mapper.repository.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 投稿に関するリポジトリの実装クラス
 */
@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final PostMapper postMapper;

    @Override
    public boolean existsById(PostId postId) {
        return postMapper.countByPostId(postId) > 0;
    }

    @Override
    public void savePost(final Post post) {

        // 投稿レコードを保存する
        final PostMapper.PostInsertEntity postEntity = PostMapper.PostInsertEntity.from(post);
        postMapper.insertPosts(postEntity); // 呼び出し後、entity.id に採番された値がセットされる

        // 画像情報の保存
        final PostMapper.PostImageInsertEntity postImageEntity =
                PostMapper.PostImageInsertEntity.from(postEntity.getId(), post);
        postMapper.insertPostImages(postImageEntity);
    }

}