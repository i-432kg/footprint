package jp.i432kg.footprint.infrastructure.seed.shared;

import jp.i432kg.footprint.application.command.service.PostCommandService;
import jp.i432kg.footprint.application.command.service.ReplyCommandService;
import jp.i432kg.footprint.application.command.service.UserCommandService;
import jp.i432kg.footprint.application.command.model.CreatePostCommand;
import jp.i432kg.footprint.application.command.model.CreateReplyCommand;
import jp.i432kg.footprint.application.command.model.CreateUserCommand;
import jp.i432kg.footprint.domain.model.ParentReply;
import jp.i432kg.footprint.domain.value.BirthDate;
import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.domain.value.FileName;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.PostComment;
import jp.i432kg.footprint.domain.value.RawPassword;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.domain.value.ReplyComment;
import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.domain.value.UserName;
import jp.i432kg.footprint.logging.LoggingEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/**
 * local / stg 共通の fixed seed scenario 投入フローを提供する基底サービスです。
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractFixedSeedService {

    private static final String USER_EMAIL_DOMAIN = "example.com";

    private final String emailPrefix;
    private final String testPassword;
    private final UserCommandService userCommandService;
    private final PostCommandService postCommandService;
    private final ReplyCommandService replyCommandService;
    private final SeedAdminOperations seedAdminOperations;
    private final SeedSourceImageLoader seedSourceImageLoader;
    private final SeedImageEntryLoader seedImageEntryLoader;
    private final Clock clock;

    /**
     * fixed seed scenario のユーザー、投稿、返信を投入します。
     */
    @Transactional
    public void seed() {
        final List<String> imageEntries = seedImageEntryLoader.loadEntries();
        final UserSeed posterWithReplies =
                createOrLoadUser("poster_with_replies", envPrefix() + "_post_reply", 1);
        final UserSeed posterWithoutReplies =
                createOrLoadUser("poster_without_replies", envPrefix() + "_post_only", 2);
        final UserSeed nonPosterReplier =
                createOrLoadUser("non_poster_replier", envPrefix() + "_reply_only", 3);

        final String postWithRepliesId = createOrLoadPost(
                posterWithReplies,
                seedCaption("post_with_replies"),
                selectImageEntry(imageEntries, 0)
        );
        createOrLoadPost(
                posterWithoutReplies,
                seedCaption("post_without_replies"),
                selectImageEntry(imageEntries, 1)
        );

        final String replyWithChildrenId = createOrLoadReply(
                postWithRepliesId,
                nonPosterReplier,
                ParentReply.root(),
                seedReplyMessage("reply_with_children")
        );
        createOrLoadReply(
                postWithRepliesId,
                posterWithReplies,
                ParentReply.of(ReplyId.of(replyWithChildrenId)),
                seedReplyMessage("reply_without_children")
        );

        log.atInfo()
                .addKeyValue("event", LoggingEvents.FIXED_SEED_SCENARIO_PREPARED)
                .addKeyValue("seedType", seedLogName())
                .log("Fixed seed scenario prepared");
    }

    protected abstract String envPrefix();

    protected abstract String seedLabel();

    protected abstract String seedLogName();

    protected abstract String missingImageEntriesMessage();

    protected abstract String imageEntryName();

    private UserSeed createOrLoadUser(final String seedKey, final String username, final int birthDay) {
        final String email = seedEmail(seedKey);

        if (seedAdminOperations.findUserIdByEmail(email).isEmpty()) {
            userCommandService.createUser(
                    CreateUserCommand.of(
                            UserName.of(username),
                            EmailAddress.of(email),
                            RawPassword.of(testPassword),
                            BirthDate.of(LocalDate.of(1990, 1, birthDay), LocalDate.now(clock))
                    )
            );
            log.atInfo()
                    .addKeyValue("event", LoggingEvents.FIXED_SEED_USER_CREATED)
                    .addKeyValue("seedType", seedLogName())
                    .addKeyValue("email", email)
                    .addKeyValue("username", username)
                    .log("Fixed seed user created");
        }

        final String userId = seedAdminOperations.findUserIdByEmail(email)
                .orElseThrow(() -> new IllegalStateException(seedLogName() + " seed user not found after create. email=" + email));

        return new UserSeed(UserId.of(userId), email);
    }

    private String createOrLoadPost(final UserSeed author, final String caption, final String imageEntry) {
        if (seedAdminOperations.findPostIdByCaption(caption).isEmpty()) {
            createPost(author.userId(), caption, imageEntry);
            log.atInfo()
                    .addKeyValue("event", LoggingEvents.FIXED_SEED_POST_CREATED)
                    .addKeyValue("seedType", seedLogName())
                    .addKeyValue("email", author.email())
                    .addKeyValue("caption", caption)
                    .addKeyValue(imageEntryName(), imageEntry)
                    .log("Fixed seed post created");
        }

        return seedAdminOperations.findPostIdByCaption(caption)
                .orElseThrow(() -> new IllegalStateException(seedLogName() + " seed post not found after create. caption=" + caption));
    }

    private String createOrLoadReply(
            final String postId,
            final UserSeed replier,
            final ParentReply parentReply,
            final String message
    ) {
        if (!seedAdminOperations.existsReplyByPostIdAndMessage(postId, message)) {
            replyCommandService.createReply(
                    CreateReplyCommand.of(
                            PostId.of(postId),
                            replier.userId(),
                            parentReply,
                            ReplyComment.of(message)
                    )
            );
            log.atInfo()
                    .addKeyValue("event", LoggingEvents.FIXED_SEED_REPLY_CREATED)
                    .addKeyValue("seedType", seedLogName())
                    .addKeyValue("postId", postId)
                    .addKeyValue("replier", replier.email())
                    .addKeyValue("message", message)
                    .log("Fixed seed reply created");
        }

        return seedAdminOperations.findReplyIdByPostIdAndMessage(postId, message)
                .orElseThrow(() -> new IllegalStateException(seedLogName() + " seed reply not found after create. postId=" + postId));
    }

    private String selectImageEntry(final List<String> imageEntries, final int preferredIndex) {
        if (imageEntries.isEmpty()) {
            throw new IllegalStateException(missingImageEntriesMessage());
        }
        return imageEntries.get(Math.min(preferredIndex, imageEntries.size() - 1));
    }

    private void createPost(final UserId userId, final String caption, final String imageEntry) {
        try (SeedSourceImage seedSourceImage = seedSourceImageLoader.load(imageEntry)) {
            postCommandService.createPost(
                    CreatePostCommand.of(
                            userId,
                            PostComment.of(caption),
                            seedSourceImage.inputStream(),
                            FileName.of(seedSourceImage.originalFilename())
                    )
            );
        } catch (IOException e) {
            throw new IllegalStateException("Failed to close " + seedLogName().toLowerCase()
                    + " seed image stream. " + imageEntryName() + "=" + imageEntry, e);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Failed to create " + seedLogName().toLowerCase()
                    + " seed post. " + imageEntryName() + "=" + imageEntry, e);
        }
    }

    private String seedEmail(final String seedKey) {
        return emailPrefix + seedKey + "@" + USER_EMAIL_DOMAIN;
    }

    private String seedCaption(final String scenarioKey) {
        return seedLabel() + " " + scenarioKey;
    }

    private String seedReplyMessage(final String scenarioKey) {
        return seedLabel() + " " + scenarioKey;
    }

    private record UserSeed(UserId userId, String email) {
    }
}
