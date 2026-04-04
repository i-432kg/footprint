package jp.i432kg.footprint.infrastructure.seed;

import jp.i432kg.footprint.application.command.PostCommandService;
import jp.i432kg.footprint.application.command.ReplyCommandService;
import jp.i432kg.footprint.application.command.UserCommandService;
import jp.i432kg.footprint.application.command.model.CreatePostCommand;
import jp.i432kg.footprint.application.command.model.CreateReplyCommand;
import jp.i432kg.footprint.application.command.model.CreateUserCommand;
import jp.i432kg.footprint.domain.value.BirthDate;
import jp.i432kg.footprint.domain.value.Comment;
import jp.i432kg.footprint.domain.value.EmailAddress;
import jp.i432kg.footprint.domain.value.FileName;
import jp.i432kg.footprint.domain.value.PostId;
import jp.i432kg.footprint.domain.value.RawPassword;
import jp.i432kg.footprint.domain.value.UserId;
import jp.i432kg.footprint.domain.value.UserName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Profile("local")
@RequiredArgsConstructor
public class LocalSeedService {

    private static final String USER_EMAIL_DOMAIN = "example.com";

    private final LocalSeedProperties properties;
    private final UserCommandService userCommandService;
    private final PostCommandService postCommandService;
    private final ReplyCommandService replyCommandService;
    private final LocalSeedAdminMapper localSeedAdminMapper;
    private final LocalSeedSourceImageProvider seedSourceImageProvider;
    private final LocalSeedImageManifestLoader localSeedImageManifestLoader;

    @Transactional
    public void seed() {
        final SeedUsers seedUsers = createSeedUsers();
        createPostsAndRepliesForActiveUsers(seedUsers.activeUsers());
    }

    private SeedUsers createSeedUsers() {
        final List<UserSeed> activeUsers = new ArrayList<>();
        final List<UserSeed> inactiveUsers = new ArrayList<>();
        int sequence = 1;

        for (int i = 1; i <= properties.getActiveUserCount(); i++) {
            activeUsers.add(createOrLoadUser(sequence, i, UserType.ACTIVE));
            sequence++;
        }

        for (int i = 1; i <= properties.getInactiveUserCount(); i++) {
            inactiveUsers.add(createOrLoadUser(sequence, i, UserType.INACTIVE));
            sequence++;
        }

        log.info("Local seed users prepared. activeUserCount={}, inactiveUserCount={}", activeUsers.size(), inactiveUsers.size());
        return new SeedUsers(activeUsers, inactiveUsers);
    }

    private void createPostsAndRepliesForActiveUsers(final List<UserSeed> activeUsers) {
        if (activeUsers.isEmpty()) {
            log.info("No active local seed users configured. Skipping post/reply creation.");
            return;
        }

        final List<String> imagePaths = localSeedImageManifestLoader.loadImagePaths();
        if (imagePaths.isEmpty()) {
            log.warn("No local seed images found. Users are created, but posts/replies are skipped.");
            return;
        }

        final int[] postSequenceByUser = new int[activeUsers.size()];

        for (int imageIndex = 0; imageIndex < imagePaths.size(); imageIndex++) {
            final int authorIndex = imageIndex % activeUsers.size();
            final UserSeed author = activeUsers.get(authorIndex);
            final int postSequence = ++postSequenceByUser[authorIndex];
            final String caption = seedCaption(author.username(), postSequence);
            final String imagePath = imagePaths.get(imageIndex);

            if (localSeedAdminMapper.findPostIdByCaption(caption).isEmpty()) {
                createPost(author.userId(), caption, imagePath);
                log.info("Local seed post created. email={}, caption={}, sourcePath={}", author.email(), caption, imagePath);
            }

            final String postId = localSeedAdminMapper.findPostIdByCaption(caption)
                    .orElseThrow(() -> new IllegalStateException("Local seed post not found after create. caption=" + caption));

            final UserSeed replier = resolveReplier(activeUsers, authorIndex);
            final String replyMessage = seedReplyMessage(postSequence, replier.username());
            if (!localSeedAdminMapper.existsReplyByPostIdAndMessage(postId, replyMessage)) {
                replyCommandService.createReply(
                        CreateReplyCommand.of(
                                PostId.of(postId),
                                replier.userId(),
                                null,
                                Comment.of(replyMessage)
                        )
                );
                log.info("Local seed reply created. postId={}, replier={}", postId, replier.email());
            }
        }
    }

    private UserSeed resolveReplier(final List<UserSeed> activeUsers, final int authorIndex) {
        if (activeUsers.size() == 1) {
            return activeUsers.get(0);
        }
        return activeUsers.get((authorIndex + 1) % activeUsers.size());
    }

    private UserSeed createOrLoadUser(final int sequence, final int typeSequence, final UserType userType) {
        final String email = seedEmail(sequence);
        final String username = seedUsername(userType, typeSequence);

        if (localSeedAdminMapper.findUserIdByEmail(email).isEmpty()) {
            userCommandService.createUser(
                    CreateUserCommand.of(
                            UserName.of(username),
                            EmailAddress.of(email),
                            RawPassword.of(properties.getTestPassword()),
                            BirthDate.of(LocalDate.of(1990, 1, Math.min(sequence, 28)))
                    )
            );
            log.info("Local seed user created. email={}, username={}, type={}", email, username, userType);
        }

        final String userId = localSeedAdminMapper.findUserIdByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Local seed user not found after create. email=" + email));

        return new UserSeed(UserId.of(userId), email, username, userType);
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

    private String seedEmail(final int number) {
        return properties.getEmailPrefix() + String.format("%02d@%s", number, USER_EMAIL_DOMAIN);
    }

    private String seedUsername(final UserType userType, final int number) {
        return String.format("local%s%02d", userType.usernamePrefix(), number);
    }

    private String seedCaption(final String username, final int postIndex) {
        return String.format("[LOCAL] %s post%02d", username, postIndex);
    }

    private String seedReplyMessage(final int postIndex, final String username) {
        return String.format("[LOCAL] %s reply%02d", username, postIndex);
    }

    private record UserSeed(UserId userId, String email, String username, UserType userType) {
    }

    private record SeedUsers(List<UserSeed> activeUsers, List<UserSeed> inactiveUsers) {
    }

    private enum UserType {
        ACTIVE("active"),
        INACTIVE("inactive");

        private final String usernamePrefix;

        UserType(final String usernamePrefix) {
            this.usernamePrefix = usernamePrefix;
        }

        private String usernamePrefix() {
            return usernamePrefix;
        }
    }
}
