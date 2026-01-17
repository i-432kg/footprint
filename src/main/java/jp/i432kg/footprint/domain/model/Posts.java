package jp.i432kg.footprint.domain.model;

import java.util.Collections;
import java.util.List;

public class Posts {

    private final List<Post> posts;

    public Posts(List<Post> posts) {
        this.posts = List.copyOf(posts);
    }

    public static Posts empty() {
        return new Posts(Collections.emptyList());
    }

    public List<Post> asList() {
        return posts;
    }

    public int size() {
        return posts.size();
    }

    public boolean isEmpty() {
        return posts.isEmpty();
    }
}
