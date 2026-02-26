package jp.i432kg.footprint.domain.repository;

import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.model.Posts;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.SearchKeyword;
import jp.i432kg.footprint.domain.value.UserId;

import java.util.Optional;

public interface PostRepository {

    void savePost(Post.NewPost newPost);

    Posts findMyPosts(UserId userId);
}
