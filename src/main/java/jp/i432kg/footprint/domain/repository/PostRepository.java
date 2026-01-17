package jp.i432kg.footprint.domain.repository;

import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.model.Posts;
import jp.i432kg.footprint.domain.value.PostId;

import java.util.Optional;

public interface PostRepository {

    Optional<Post> findPost(final PostId postId);

    Posts findRecentPosts();

    void savePost(final Post.NewPost newPost);
}
