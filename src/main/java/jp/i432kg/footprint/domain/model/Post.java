package jp.i432kg.footprint.domain.model;

import jp.i432kg.footprint.domain.value.*;
import lombok.*;

import java.time.LocalDateTime;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class Post {

    @org.jspecify.annotations.NonNull
    PostId id;

    @org.jspecify.annotations.NonNull
    UserId userId;

    @org.jspecify.annotations.NonNull
    ImageFileName imageFileName;

    @org.jspecify.annotations.NonNull
    Comment caption;

    boolean hasLocation;

    @org.jspecify.annotations.NonNull
    Location location;

    @org.jspecify.annotations.NonNull
    LocalDateTime createdAt;

    @org.jspecify.annotations.NonNull
    LocalDateTime updatedAt;

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
