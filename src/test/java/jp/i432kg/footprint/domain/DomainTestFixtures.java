package jp.i432kg.footprint.domain;

import jp.i432kg.footprint.domain.model.Image;
import jp.i432kg.footprint.domain.model.BoundingBox;
import jp.i432kg.footprint.domain.model.Location;
import jp.i432kg.footprint.domain.model.ParentReply;
import jp.i432kg.footprint.domain.model.Post;
import jp.i432kg.footprint.domain.model.Reply;
import jp.i432kg.footprint.domain.model.User;
import jp.i432kg.footprint.domain.value.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class DomainTestFixtures {

    public static final String USER_ID = "01ARZ3NDEKTSV4RRFFQ69G5FAV";
    public static final String OTHER_USER_ID = "01ARZ3NDEKTSV4RRFFQ69G5FAW";
    public static final String POST_ID = "01ARZ3NDEKTSV4RRFFQ69G5FAX";
    public static final String OTHER_POST_ID = "01ARZ3NDEKTSV4RRFFQ69G5FAY";
    public static final String REPLY_ID = "01ARZ3NDEKTSV4RRFFQ69G5FAZ";
    public static final String OTHER_REPLY_ID = "01ARZ3NDEKTSV4RRFFQ69G5FB0";
    public static final String IMAGE_ID = "01ARZ3NDEKTSV4RRFFQ69G5FB1";

    private DomainTestFixtures() {
    }

    public static UserId userId() {
        return UserId.of(USER_ID);
    }

    public static UserId otherUserId() {
        return UserId.of(OTHER_USER_ID);
    }

    public static PostId postId() {
        return PostId.of(POST_ID);
    }

    public static PostId otherPostId() {
        return PostId.of(OTHER_POST_ID);
    }

    public static ReplyId replyId() {
        return ReplyId.of(REPLY_ID);
    }

    public static ReplyId otherReplyId() {
        return ReplyId.of(OTHER_REPLY_ID);
    }

    public static ImageId imageId() {
        return ImageId.of(IMAGE_ID);
    }

    public static FileExtension fileExtension() {
        return FileExtension.of("jpg");
    }

    public static ObjectKey objectKey() {
        return ObjectKey.of("users/" + USER_ID + "/posts/" + POST_ID + "/images/" + IMAGE_ID + ".jpg");
    }

    public static StorageObject storageObject() {
        return StorageObject.local(objectKey());
    }

    public static jp.i432kg.footprint.domain.value.Byte fileSize() {
        return jp.i432kg.footprint.domain.value.Byte.of(1024L);
    }

    public static Pixel width() {
        return Pixel.of(640);
    }

    public static Pixel height() {
        return Pixel.of(480);
    }

    public static Latitude latitude() {
        return Latitude.of(new BigDecimal("35.681236"));
    }

    public static Longitude longitude() {
        return Longitude.of(new BigDecimal("139.767125"));
    }

    public static Location location() {
        return Location.of(latitude(), longitude());
    }

    public static BoundingBox boundingBox() {
        return BoundingBox.of(
                latitude(),
                Latitude.of(new BigDecimal("35.700000")),
                longitude(),
                Longitude.of(new BigDecimal("139.800000"))
        );
    }

    public static Image image() {
        return Image.of(
                storageObject(),
                fileExtension(),
                fileSize(),
                width(),
                height(),
                location(),
                true,
                LocalDateTime.of(2026, 4, 1, 12, 30)
        );
    }

    public static PostComment caption() {
        return PostComment.of("hello footprint");
    }

    public static ReplyComment replyMessage() {
        return ReplyComment.of("nice photo");
    }

    public static Post post() {
        return Post.of(
                postId(),
                userId(),
                image(),
                caption(),
                LocalDateTime.of(2026, 4, 1, 13, 0)
        );
    }

    public static Reply reply() {
        return Reply.of(
                replyId(),
                postId(),
                userId(),
                ParentReply.root(),
                replyMessage(),
                LocalDateTime.of(2026, 4, 1, 13, 30)
        );
    }

    public static User user() {
        return User.of(
                userId(),
                UserName.of("user_01"),
                EmailAddress.of("user@example.com"),
                HashedPassword.of("hashed-password"),
                BirthDate.restore(LocalDate.of(2000, 1, 1))
        );
    }
}
