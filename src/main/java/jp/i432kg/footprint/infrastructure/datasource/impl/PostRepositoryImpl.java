package jp.i432kg.footprint.infrastructure.datasource.impl;

import jp.i432kg.footprint.domain.model.Location;
import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.model.Posts;
import jp.i432kg.footprint.domain.repository.PostRepository;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.SearchKeyword;
import jp.i432kg.footprint.infrastructure.datasource.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final PostMapper postMapper;

    @Override
    public Optional<Post> findPost(PostId postId) {
        return postMapper.findPostByPostId(postId)
                .map(this::ensureLocation);
    }

    @Override
    public Posts findRecentPosts() {
        final List<Post> posts = postMapper.findRecentPosts().stream()
                .map(this::ensureLocation)
                .toList();
        return new Posts(posts);
    }

    @Override
    public void savePost(final Post.NewPost newPost) {
        postMapper.insert(
                newPost.getUserId(),
                newPost.getImageFileName(),
                newPost.getComment(),
                newPost.getLocation().getLatitude().orElse(null),
                newPost.getLocation().getLongitude().orElse(null)
        );
    }

    @Override
    public Posts search(final SearchKeyword keyword, final PostId lastId, final int size) {
        final List<Post> posts = postMapper.searchPosts(keyword, lastId, size).stream()
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
                .imageFileName(post.getImageFileName())
                .comment(post.getComment())
                .createdAt(post.getCreatedAt())
                .location(Location.unknown())
                .build();
    }

}
