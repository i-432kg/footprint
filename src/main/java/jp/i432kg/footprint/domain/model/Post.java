package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.value.*;
import lombok.*;

import java.time.LocalDateTime;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class Post {

    /**
     * 投稿 ID
     */
    PostId id;

    /**
     * 投稿者
     */
    UserId userId;

    /**
     * 投稿画像
     */
    Image image;

    /**
     * 投稿コメント
     */
    Comment caption;

    /**
     * 投稿位置情報
     */
    Location location;

    /**
     * 投稿時間
     */
    LocalDateTime createdAt;

    @Value
    @Builder(toBuilder = true)
    public static class NewPost {
        UserId userId;
        ImageFileName imageFileName;
        Comment comment;
        Location location;

        public NewPost withImage(ImageFileName fileName) {
            return this.toBuilder().imageFileName(fileName).build();
        }

        public NewPost withLocation(Location location) {
            return this.toBuilder().location(location).build();
        }
    }

    public static NewPost.NewPostBuilder newPost() {
        return NewPost.builder();
    }
}
