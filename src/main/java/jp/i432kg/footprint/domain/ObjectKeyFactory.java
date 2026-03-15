package jp.i432kg.footprint.domain;

import jp.i432kg.footprint.domain.value.*;

import java.util.Objects;

/**
 * オブジェクトキーのファクトリクラス
 * <p>
 * フォーマット：
 * users/{userId}/posts/{postId}/images/{imageId}
 * <p>
 * 例：
 * users/01HN.../posts/01HP.../images/01HQ....jpg
 *
 */
public class ObjectKeyFactory {

    private static final String USER_SEGMENT = "users";
    private static final String POST_SEGMENT = "posts";
    private static final String IMAGE_SEGMENT = "images";

    public static ObjectKey createPostImageKey(
            UserId userId,
            PostId postId,
            ImageId imageId,
            FileExtension extension
    ) {

        if (Objects.isNull(userId)) {
            throw new IllegalArgumentException("userId must not be null");
        }

        if (Objects.isNull(postId)) {
            throw new IllegalArgumentException("postId must not be null");
        }

        if (Objects.isNull(imageId)) {
            throw new IllegalArgumentException("imageId must not be null");
        }

        if (Objects.isNull(extension)) {
            throw new IllegalArgumentException("extension must not be null");
        }

        final String key = String.format(
                "%s/%s/%s/%s/%s/%s.%s",
                USER_SEGMENT,
                userId.getValue(),
                POST_SEGMENT,
                postId.getValue(),
                IMAGE_SEGMENT,
                imageId.getValue(),
                extension.getValue()
        );

        return ObjectKey.of(key);
    }
}