package jp.i432kg.footprint.infrastructure.seed.local;

import jp.i432kg.footprint.application.command.PostCommandService;
import jp.i432kg.footprint.application.command.ReplyCommandService;
import jp.i432kg.footprint.application.command.UserCommandService;
import jp.i432kg.footprint.application.command.model.CreatePostCommand;
import jp.i432kg.footprint.application.command.model.CreateReplyCommand;
import jp.i432kg.footprint.application.command.model.CreateUserCommand;
import jp.i432kg.footprint.domain.model.ParentReply;
import jp.i432kg.footprint.domain.value.BirthDate;
import jp.i432kg.footprint.domain.value.Comment;
import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.domain.value.FileName;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.RawPassword;
import jp.i432kg.footprint.domain.value.ReplyId;
import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.domain.value.UserName;
import jp.i432kg.footprint.infrastructure.seed.shared.SeedSourceImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * local 環境向けの固定シナリオ seed データを投入するサービスです。
 * <p>
 * 投稿あり/なし、返信あり/なしの状態を確認できる最小データセットを維持します。
 * </p>
 */
@Slf4j
@Service
@Profile("local")
@RequiredArgsConstructor
public class LocalSeedService {

    private static final String USER_EMAIL_DOMAIN = "example.com";
    private static final String LOCAL_LABEL = "[LOCAL]";

    private final LocalSeedProperties properties;
    private final UserCommandService userCommandService;
    private final PostCommandService postCommandService;
    private final ReplyCommandService replyCommandService;
    private final LocalSeedAdminMapper localSeedAdminMapper;
    private final LocalSeedSourceImageProvider seedSourceImageProvider;
    private final LocalSeedImageManifestLoader localSeedImageManifestLoader;

    /**
     * local 固定シナリオの seed ユーザー、投稿、返信を投入します。
     */
    @Transactional
    public void seed() {
        final List<String> imagePaths = localSeedImageManifestLoader.loadImagePaths();
        final UserSeed posterWithReplies = createOrLoadUser("poster_with_replies", "loc_post_reply", 1);
        final UserSeed posterWithoutReplies = createOrLoadUser("poster_without_replies", "loc_post_only", 2);
        final UserSeed nonPosterReplier = createOrLoadUser("non_poster_replier", "loc_reply_only", 3);

        final String postWithRepliesId = createOrLoadPost(
                posterWithReplies,
                seedCaption("post_with_replies"),
                selectImagePath(imagePaths, 0)
        );
        createOrLoadPost(
                posterWithoutReplies,
                seedCaption("post_without_replies"),
                selectImagePath(imagePaths, 1)
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

        log.info("Local fixed seed scenario prepared.");
    }

    private UserSeed createOrLoadUser(final String seedKey, final String username, final int birthDay) {
        final String email = seedEmail(seedKey);

        if (localSeedAdminMapper.findUserIdByEmail(email).isEmpty()) {
            userCommandService.createUser(
                    CreateUserCommand.of(
                            UserName.of(username),
                            EmailAddress.of(email),
                            RawPassword.of(properties.getTestPassword()),
                            BirthDate.of(LocalDate.of(1990, 1, birthDay))
                    )
            );
            log.info("Local seed user created. email={}, username={}", email, username);
        }

        final String userId = localSeedAdminMapper.findUserIdByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Local seed user not found after create. email=" + email));

        return new UserSeed(UserId.of(userId), email, username);
    }

    private String createOrLoadPost(final UserSeed author, final String caption, final String imagePath) {
        if (localSeedAdminMapper.findPostIdByCaption(caption).isEmpty()) {
            createPost(author.userId(), caption, imagePath);
            log.info("Local seed post created. email={}, caption={}, sourcePath={}", author.email(), caption, imagePath);
        }

        return localSeedAdminMapper.findPostIdByCaption(caption)
                .orElseThrow(() -> new IllegalStateException("Local seed post not found after create. caption=" + caption));
    }

    private String createOrLoadReply(
            final String postId,
            final UserSeed replier,
            final ParentReply parentReply,
            final String message
    ) {
        if (!localSeedAdminMapper.existsReplyByPostIdAndMessage(postId, message)) {
            replyCommandService.createReply(
                    CreateReplyCommand.of(
                            PostId.of(postId),
                            replier.userId(),
                            parentReply,
                            Comment.of(message)
                    )
            );
            log.info("Local seed reply created. postId={}, replier={}, message={}", postId, replier.email(), message);
        }

        return localSeedAdminMapper.findReplyIdByPostIdAndMessage(postId, message)
                .orElseThrow(() -> new IllegalStateException("Local seed reply not found after create. postId=" + postId));
    }

    private String selectImagePath(final List<String> imagePaths, final int preferredIndex) {
        if (imagePaths.isEmpty()) {
            throw new IllegalStateException("Local seed requires at least one source image.");
        }
        return imagePaths.get(Math.min(preferredIndex, imagePaths.size() - 1));
    }

    private void createPost(final UserId userId, final String caption, final String imagePath) {
        try (SeedSourceImage seedSourceImage = seedSourceImageProvider.load(imagePath)) {
            postCommandService.createPost(
                    CreatePostCommand.of(
                            userId,
                            Comment.of(caption),
                            seedSourceImage.inputStream(),
                            FileName.of(seedSourceImage.originalFilename())
                    )
            );
        } catch (IOException e) {
            throw new IllegalStateException("Failed to close local seed image stream. path=" + imagePath, e);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Failed to create local seed post. sourcePath=" + imagePath, e);
        }
    }

    private String seedEmail(final String seedKey) {
        return properties.getEmailPrefix() + seedKey + "@" + USER_EMAIL_DOMAIN;
    }

    private String seedCaption(final String scenarioKey) {
        return LOCAL_LABEL + " " + scenarioKey;
    }

    private String seedReplyMessage(final String scenarioKey) {
        return LOCAL_LABEL + " " + scenarioKey;
    }

    private record UserSeed(UserId userId, String email, String username) {
    }
}
