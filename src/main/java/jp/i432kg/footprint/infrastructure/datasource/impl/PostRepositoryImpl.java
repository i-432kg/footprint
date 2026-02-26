package jp.i432kg.footprint.infrastructure.datasource.impl;

import jp.i432kg.footprint.domain.model.Location;
import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.model.Posts;
import jp.i432kg.footprint.domain.repository.PostRepository;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.SearchKeyword;
import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.infrastructure.datasource.mapper.PostImagesMapper;
import jp.i432kg.footprint.infrastructure.datasource.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final PostMapper postMapper;

    private final PostImagesMapper postImagesMapper;

//    @Override
//    public Optional<Post> findPost(PostId postId) {
//        return postMapper.findPostByPostId(postId)
//                .map(this::ensureLocation);
//    }
//
//    @Override
//    public Posts findRecentPosts(final PostId lastId, final int size) {
//        final List<Post> posts = postMapper.findRecentPosts(lastId, size).stream()
//                .map(this::ensureLocation)
//                .toList();
//        return new Posts(posts);
//    }

    @Override
    public void savePost(final Post.NewPost newPost) {

        // 投稿レコードを保存する
        final PostMapper.PostInsertEntity entity = PostMapper.PostInsertEntity.from(newPost);
        postMapper.insert(entity); // 呼び出し後、entity.id に採番された値がセットされる

        // 自動採番されたIDを PostId に変換 (Long -> int)
        final PostId generatedPostId = new PostId(entity.getId().intValue());

        // 画像情報の保存
        final PostImagesMapper.PostImageInsertEntity imageEntity =
                PostImagesMapper.PostImageInsertEntity.from(
                        generatedPostId,
                        newPost.getImageFileName(),
                        newPost.getLocation().hasLocation()
                );
        postImagesMapper.insert(imageEntity);
    }

//    @Override
//    public Posts search(final SearchKeyword keyword, final PostId lastId, final int size) {
//        final List<Post> posts = postMapper.searchPosts(keyword, lastId, size).stream()
//                .map(this::ensureLocation)
//                .toList();
//        return new Posts(posts);
//    }

    @Override
    public Posts findMyPosts(final UserId userId) {
        final List<Post> posts = postMapper.findPostByUserId(userId).stream()
                .map(this::ensureLocation)
                .toList();
        return new Posts(posts);
    }

    /**
     * "Mybatisで取得した値がnullのとき親オブジェクトもnullにしてしまう仕様を解消する"
     *
     * @param post
     * @return
     * // TODO この処理がなくてもいいように修正する
     */
    private Post ensureLocation(final Post post) {
        if (post.getLocation() != null) {
            return post;
        }

        return Post.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .image(post.getImage())
                .caption(post.getCaption())
                .createdAt(post.getCreatedAt())
                .location(Location.unknown())
                .build();
    }

}
